package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.sort.distribute.Sortcheck;
import com.zdd.bdc.sort.util.Sortstatus;

public class Sortcheckimpl implements Sortcheck {

	@Override
	public String check(Path sortingfolder, int bigfilehash) throws Exception {
		String digname = sortingfolder.getParent().getParent().getParent().getParent().getParent().getFileName()
				.toString();
		String namespace = sortingfolder.getParent().getParent().getParent().getParent().getFileName().toString();
		String table = sortingfolder.getParent().getParent().getParent().getFileName().toString();
		String col = sortingfolder.getParent().getParent().getFileName().toString();
		String filters = sortingfolder.getParent().getFileName().toString();
		Path datafolder = Filekvutil.datafolder(namespace, table, col);
		String[] datafiles = datafolder.toFile().list();
		StringBuffer error = new StringBuffer();
		StringBuffer found = new StringBuffer();
		for (String datafile : datafiles) {
			Filekvutil.walkdata(datafolder.resolve(datafile), new Filedatawalk() {
				@Override
				public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
						boolean isv1deleted, byte[] v2, boolean isv2deleted) {
					if (!Configclient.running) {
						error.append(new Date() + " ==== shutdown this server");
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null, null);
					} else {
						if (isv1deleted || isv2deleted) {
							return null;
						} else {
							if (Sortstatus.get(sortingfolder) != null
									&& !Sortstatus.get(sortingfolder).equals(Sortstatus.SORT_NOTINCLUDED)
									&& !Sortstatus.get(sortingfolder).equals(Sortstatus.TERMINATE)) {
								found.append("found");
								return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
										Filedatawalkresult.DATA_DONOTHING, null, null);
							} else {
								try {
									String key = STATIC.tostring(v1);
									if (filters.equals(Digging.getfilters(key, namespace, digname, bigfilehash))) {
										found.append("found");
										return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
												Filedatawalkresult.DATA_DONOTHING, null, null);
									} else {
										// do nothing
									}
									return null;
								} catch (Exception e) {
									error.append(STATIC.strackstring(e));
									return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
											Filedatawalkresult.DATA_DONOTHING, null, null);
								}
							}
						}
					}
				}

			}, true);
			if (error.length() == 0) {
				if (found.length() == 0) {
					// do nothing
				} else {
					return Sortstatus.SORT_INCLUDED;
				}
			} else {
				throw new Exception(error.toString());
			}
		}
		if (error.length() == 0) {
			if (found.length() == 0) {
				return Sortstatus.SORT_NOTINCLUDED;
			} else {
				return Sortstatus.SORT_INCLUDED;
			}
		} else {
			throw new Exception(error.toString());
		}
	}

}
