package com.zdd.bdc.biz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Fileutil;
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
		return !STATIC.SORT_SEQUENCE(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_DIG).read(digname + ".sequence"));
	}

	@Override
	protected int prepareonefilecapacity() {
		return 1000000;
	}

	@Override
	protected Path preparesortingfolder() throws Exception {
		return Paths.get(STATIC.REMOTE_CONFIG_DIG).resolve(digname).resolve(namespace)
				.resolve(table).resolve(col).resolve(filters).resolve(version);
	}

	@Override
	protected void preparedatasource() throws Exception {
		Path datafolder = Filekvutil.datafolder(namespace, table, col);
		String[] datafiles = datafolder.toFile().list();
		for (String datafile : datafiles) {
			StringBuffer error = new StringBuffer();
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
								Long amount = null;
								try {
									amount = Long.parseLong(STATIC.tostring(v2));
								} catch (Exception e) {
									amount = (long) STATIC.tostring(v2).compareTo(STATIC.SORT_COMPARE_TO_STRING);
								}
								input(key, amount);
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
				// do nothing
			} else {
				throw new Exception(error.toString());
			}
		}

	}

}
