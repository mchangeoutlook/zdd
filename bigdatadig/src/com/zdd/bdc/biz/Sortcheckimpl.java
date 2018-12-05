package com.zdd.bdc.biz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Fileutil;
import com.zdd.bdc.sort.distribute.Sortcheck;
import com.zdd.bdc.sort.util.Sortstatus;

public class Sortcheckimpl implements Sortcheck{

	@Override
	public String check(Path sortingfolder, int bigfilehash) throws Exception {
		String digname = sortingfolder.getParent().getParent().getParent().getParent().getParent().getFileName().toString();		
		String namespace = sortingfolder.getParent().getParent().getParent().getParent().getFileName().toString();		
		String table = sortingfolder.getParent().getParent().getParent().getFileName().toString();		
		String col = sortingfolder.getParent().getParent().getFileName().toString();		
		String filters = sortingfolder.getParent().getFileName().toString();		
		Path datafolder = Filekvutil.datafolder(namespace, table, col);
		String[] datafiles = datafolder.toFile().list();
		for (String datafile : datafiles) {
			StringBuffer error = new StringBuffer();
			StringBuffer found = new StringBuffer();
			Fileutil.walkdata(datafolder.resolve(datafile), new Filedatawalk() {

				@Override
				public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
						boolean isv1deleted, byte[] v2, boolean isv2deleted) {
					if (isv1deleted || isv2deleted) {
						return null;
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
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							error.append(errors.toString());
							return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
									Filedatawalkresult.DATA_DONOTHING, null, null);
						}
					}
				}

			}, true);
			if (error.length() == 0) {
				if (found.length()==0) {
					return Sortstatus.SORT_NOTINCLUDED;
				} else {
					return Sortstatus.SORT_INCLUDED;
				}
			} else {
				throw new Exception(error.toString());
			}
		}
		return Sortstatus.SORT_NOTINCLUDED;
	}

}
