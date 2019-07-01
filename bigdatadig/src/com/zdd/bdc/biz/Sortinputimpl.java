package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatautil;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.sort.local.Sortinput;

public class Sortinputimpl extends Sortinput {

	private String namespace = null;
	private String digname = null;
	private String table = null;
	private String col = null;
	private String filters = null;
	private String version = null;
	private int bigfilehash = -1;

	public Sortinputimpl(String thedigname, String thenamespace, String thetable, String thecol, String thefilters,
			String theversion, int thebigfilehash) {
		namespace = thenamespace;
		digname = thedigname;
		table = thetable;
		col = thecol;
		filters = thefilters;
		version = theversion;
		bigfilehash = thebigfilehash;
	}

	@Override
	protected boolean prepareisasc() {
		return STATIC.SORT_SEQUENCE(
				Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG).read(digname + ".sequence"));
	}

	@Override
	protected int prepareonefilecapacity() {
		return 200000;
	}

	@Override
	protected Path preparesortingfolder() throws Exception {
		return Paths.get(STATIC.REMOTE_CONFIGFILE_DIG).resolve(digname).resolve(namespace).resolve(table).resolve(col)
				.resolve(filters).resolve(version);
	}

	@Override
	protected void preparedatasource() throws Exception {
		Path datafolder = Filedatautil.folder(namespace, table, col);
		String[] datafiles = datafolder.toFile().list();
		for (String datafile : datafiles) {
			StringBuffer error = new StringBuffer();
			Filekvutil.walkdata(STATIC.keylength, datafolder.resolve(datafile), new Filedatawalk() {

				@Override
				public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value, boolean isvaluedeleted) {
					if (isvaluedeleted) {
						return null;
					} else {
						if (!Configclient.running()) {
							error.append(new Date() + " ==== shutdown this server");
							return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
									Filedatawalkresult.DATA_DONOTHING, null, null);
						} else {
							try {
								if (filters.equals(Digging.getfilters(key, namespace, digname, bigfilehash))) {
									Long amount = null;
									try {
										amount = Long.parseLong(value);
									} catch (Exception e) {
										amount = (long) value.compareTo(STATIC.SORT_COMPARE_TO_STRING);
									}
									input(key, amount);
								} else {
									// do nothing
								}
								return null;
							} catch (Exception e) {
								error.append(STATIC.stackstring(e));
								return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
										Filedatawalkresult.DATA_DONOTHING, null, null);
							}
						}
					}
				}

			});
			if (error.length() == 0) {
				// do nothing
			} else {
				throw new Exception(error.toString());
			}
		}

	}

}
