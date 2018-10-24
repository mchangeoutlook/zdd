package com.zdd.bdc.util;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class Filekvutil {
	public static final String CHARSET_DEFAULT = "UTF-8";
	public static final Path LOCAL_CONFIGFOLDER = Paths.get("config");
	public static final Path LOCAL_DATAFOLDER = Paths.get(".").toAbsolutePath().getParent().getParent().resolve("data");
	
	private static final int configkeymaxlength = 100;
	private static final int configvalmaxlength = 500;
	private static final int configmaxnumofvals = 100;

	private static final String versionkey = ":version:";
	private static final int versionkeymaxlength = 9;
	private static final int versionvalmaxlength = 50;

	private static final int incrementmaxlength = String.valueOf(Long.MAX_VALUE).length()+1;

	public static void config(String key, String value, String namespace, String configfile) throws Exception {
		Fileutil.create(tobytes(key), configkeymaxlength, tobytes(value), configvalmaxlength, configfile(namespace, configfile));
	}

	public static String config(String key, String namespace, String configfile) throws Exception {
		byte[] val = Fileutil.readfirstvalue2byvalue1(tobytes(key), configfile(namespace, configfile));
		if (val == null) {
			return null;
		} else {
			return tostring(val);
		}
	}

	public static String[] configs(String key, String namespace, String configfile) throws Exception {
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(tobytes(key), configmaxnumofvals, true, configfile(namespace, configfile));
		String[] returnvalue = new String[vals.size()];
		for (int i = 0; i < vals.size(); i++) {
			returnvalue[i] = tostring(vals.get(i));
		}
		return returnvalue;
	}

	public static String indexversion(Path target) throws Exception {
		Vector<byte[]> val = Fileutil.readfirstvalue1value2(target);
		if (val.isEmpty()) {
			return null;
		} else {
			if (versionkey.equals(tostring(val.get(0)))) {
				return tostring(val.get(1));
			} else {
				return null;
			}
		}
	}

	public static void indexversion(String version, Path target) throws Exception {
		synchronized (Fileutil.syncfile(target)) {
			if (!Files.exists(target) || Files.size(target) == 0) {
				Fileutil.create(tobytes(versionkey), versionkeymaxlength, tobytes(version), versionvalmaxlength,
						target);
			} else {
				String existingversion = indexversion(target);
				if (existingversion == null) {
					throw new Exception("noexistversion");
				} else {
					Fileutil.modifyfirstvalue2byvalue1(tobytes(versionkey), tobytes(version), target);
				}
			}
		}
	}

	public static String index(String index, Vector<String> filters, int bigfilehash, Path indexfolder) throws Exception {
		
		Path target = indexfile(index, -1, filters, bigfilehash, indexfolder);
		
		byte[] val = Fileutil.readlastvalue2byvalue1(tobytes(index), target);
		if (val == null) {
			return null;
		} else {
			return tostring(val);
		}
	}

	public static String[] indexes(String index, int numofvals, long pagenum, Vector<String> filters, int bigfilehash, Path indexfolder) throws Exception {
		Path target = indexfile(index, pagenum, filters, bigfilehash, indexfolder);
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(tobytes(index), numofvals, true, target);
		String[] returnvalue = new String[vals.size()];
		for (int i = 0; i < vals.size(); i++) {
			returnvalue[i] = tostring(vals.get(i));
		}
		return returnvalue;
	}

	public static void index(String index, String value, boolean isunique, long pagenum, Vector<String> filters, int bigfilehash, Path indexfolder) throws Exception {
		if (isunique) {
			Path target = indexfile(index, -1, filters, bigfilehash, indexfolder);
			synchronized (Fileutil.synckey(index)) {
				if (index(index, filters, bigfilehash, indexfolder) == null) {
					byte[] indexb = tobytes(index);
					byte[] valueb = tobytes(value);
					Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
				} else {
					throw new Exception("duplicate");
				}
			}
		} else {
			Path target = indexfile(index, pagenum, filters, bigfilehash, indexfolder);
			byte[] indexb = tobytes(index);
			byte[] valueb = tobytes(value);
			Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
		}

	}

	public static String dataread(String key, String namespace, String table, String column, int bigfilehash) throws Exception {
		byte[] val = Fileutil.readlastvalue2byvalue1(tobytes(key), datafile(key, bigfilehash, datafolder(namespace, table, column)));
		if (val == null) {
			return null;
		} else {
			return tostring(val);
		}
	}

	public static void datadelete(String key, String namespace, String table, String column, int bigfilehash) throws Exception {
		Fileutil.deletelastvalue2byvalue1(tobytes(key), datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datamodify(String key, String newvalue, String namespace, String table, String column, int bigfilehash) throws Exception {
		Fileutil.modifylastvalue2byvalue1(tobytes(key), tobytes(newvalue), datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datacreate(String key, String value, int valuemaxlength, String namespace, String table, String column, int bigfilehash) throws Exception {
		byte[] keyb = tobytes(key);
		Fileutil.create(keyb, keyb.length, tobytes(value), valuemaxlength, datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static long dataincrement(String key, long amount, String namespace, String table, String column, int bigfilehash) throws Exception {
		synchronized (Fileutil.synckey(key)) {
			String amountstr = dataread(key, namespace, table, column, bigfilehash);
			if (amountstr == null) {
				byte[] keyb = tobytes(key);
				Fileutil.create(keyb, keyb.length, tobytes(String.valueOf(amount)), incrementmaxlength, datafile(key, bigfilehash, datafolder(namespace, table, column)));
				return amount;
			} else {
				Long oldval = null;
				try {
					oldval = Long.parseLong(amountstr);
				} catch (Exception e) {
					throw new Exception("nolongvalue");
				}
				long newamount = oldval + amount;
				byte[] keyb = tobytes(key);
				Fileutil.modifylastvalue2byvalue1(keyb, tobytes(String.valueOf(newamount)), datafile(key, bigfilehash, datafolder(namespace, table, column)));
				return newamount;
			}
		}
	}

	public static byte[] tobytes(String str) throws Exception {
		return str.getBytes(CHARSET_DEFAULT);
	}

	public static String tostring(byte[] b) throws Exception {
		return new String(b, CHARSET_DEFAULT);
	}

	public static Path datafile(String key, int bigfilehash, Path datafolder) {
		return datafolder.resolve(String.valueOf(Math.abs(key.hashCode()) % bigfilehash));
	}

	public static Path datafolder(String namespace, String table, String column) {
		if (namespace.trim().isEmpty() || table.trim().isEmpty() || column.trim().isEmpty()) {
			return null;
		} else {
			return LOCAL_DATAFOLDER.resolve(namespace).resolve(table).resolve(column);
		}
	}

	public static Path indexfile(String index, long pagenum, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		String filtersandpagenum = "";
		if (filters != null && !filters.isEmpty()) {
			for (String f : filters) {
				if (!f.trim().isEmpty()) {
					filtersandpagenum += URLEncoder.encode(f, CHARSET_DEFAULT) + "#";
				} else {
					throw new Exception("hasemptyfilter");
				}
			}
		}
		if (filtersandpagenum.isEmpty()) {
			filtersandpagenum+="#"+String.valueOf(pagenum);
		} else {
			filtersandpagenum+=String.valueOf(pagenum);
		}
		return indexfolder.resolve(filtersandpagenum).resolve(String.valueOf(Math.abs(index.hashCode()) % bigfilehash));

	}

	public static Path configfile(String namespace, String configfile) {
		return LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
	}

}
