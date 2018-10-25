package com.zdd.bdc.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

public class Filekvutil {

	public static void config(String key, String value, String namespace, String configfile) throws Exception {
		Fileutil.create(SS.tobytes(key), SS.configkeymaxlength, SS.tobytes(value), SS.configvalmaxlength,
				configfile(namespace, configfile));
	}

	public static String config(String key, String namespace, String configfile) throws Exception {
		byte[] val = Fileutil.readfirstvalue2byvalue1(SS.tobytes(key), configfile(namespace, configfile));
		if (val == null) {
			return null;
		} else {
			return SS.tostring(val);
		}
	}

	public static Vector<String> configs(String key, String namespace, String configfile) throws Exception {
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(SS.tobytes(key), SS.configmaxnumofvals, true,
				configfile(namespace, configfile));
		Vector<String> returnvalue = new Vector<String>(vals.size());
		for (byte[] val:vals) {
			returnvalue.add(SS.tostring(val));
		}
		return returnvalue;
	}

	public static String indexversion(Path target) throws Exception {
		Vector<byte[]> val = Fileutil.readfirstvalue1value2(target);
		if (val.isEmpty()) {
			return null;
		} else {
			if (SS.versionkey.equals(SS.tostring(val.get(0)))) {
				return SS.tostring(val.get(1));
			} else {
				return null;
			}
		}
	}

	public static void indexversion(String version, String index, String value, Long pagenum, Vector<String> filters,
			int bigfilehash, Path indexfolder) throws Exception {
		
		Path target = indexfile(index, pagenum, filters, bigfilehash, indexfolder);

		synchronized (SS.syncfile(target)) {
			if (!Files.exists(target) || Files.size(target) == 0) {
				Fileutil.create(SS.tobytes(SS.versionkey), SS.versionkeymaxlength, SS.tobytes(version),
						SS.versionvalmaxlength, target);
			} else {
				String existingversion = indexversion(target);
				if (existingversion == null) {
					throw new Exception("noexistversion");
				} else if (existingversion.compareTo(version)<0){
					Files.write(target, new byte[0], StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
					Fileutil.create(SS.tobytes(SS.versionkey), SS.versionkeymaxlength, SS.tobytes(version),
							SS.versionvalmaxlength, target);
				} else if (existingversion.compareTo(version)>0){
					throw new Exception("olderversion");
				} else {
					//do nothing
				}
			}
			index(index, value, pagenum, filters,
				 bigfilehash, indexfolder);
		}
	}

	public static String index(String index, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {

		Path target = indexfile(index, null, filters, bigfilehash, indexfolder);

		byte[] val = Fileutil.readlastvalue2byvalue1(SS.tobytes(index), target);
		if (val == null) {
			return null;
		} else {
			return SS.tostring(val);
		}
	}

	public static Vector<String> indexes(String index, int numofvals, Long pagenum, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		Path target = indexfile(index, pagenum, filters, bigfilehash, indexfolder);
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(SS.tobytes(index), numofvals, true, target);
		Vector<String> returnvalue = new Vector<String>(vals.size());
		for (byte[] val:vals) {
			returnvalue.add(SS.tostring(val));
		}
		return returnvalue;
	}

	public static void index(String index, String value, Long pagenum, Vector<String> filters,
			int bigfilehash, Path indexfolder) throws Exception {
		Path target = indexfile(index, pagenum, filters, bigfilehash, indexfolder);
		byte[] indexb = SS.tobytes(index);
		byte[] valueb = SS.tobytes(value);
		if (pagenum==null) {
			synchronized (SS.synckey(index)) {
				if (index(index, filters, bigfilehash, indexfolder) == null) {
					Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
				} else {
					throw new Exception("duplicate");
				}
			}
		} else {
			Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
		}

	}

	public static String dataread(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		byte[] val = Fileutil.readlastvalue2byvalue1(SS.tobytes(key),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
		if (val == null) {
			return null;
		} else {
			return SS.tostring(val);
		}
	}

	public static void datadelete(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		Fileutil.deletelastvalue2byvalue1(SS.tobytes(key),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datamodify(String key, String newvalue, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		Fileutil.modifylastvalue2byvalue1(SS.tobytes(key), SS.tobytes(newvalue),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datacreate(String key, String value, int valuemaxlength, String namespace, String table,
			String column, int bigfilehash) throws Exception {
		byte[] keyb = SS.tobytes(key);
		Fileutil.create(keyb, keyb.length, SS.tobytes(value), valuemaxlength,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static long dataincrement(String key, long amount, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		synchronized (SS.synckey(key)) {
			String amountstr = dataread(key, namespace, table, column, bigfilehash);
			if (amountstr == null) {
				byte[] keyb = SS.tobytes(key);
				Fileutil.create(keyb, keyb.length, SS.tobytes(String.valueOf(amount)), SS.incrementmaxlength,
						datafile(key, bigfilehash, datafolder(namespace, table, column)));
				return amount;
			} else {
				Long oldval = null;
				try {
					oldval = Long.parseLong(amountstr);
				} catch (Exception e) {
					throw new Exception("nolongvalue");
				}
				long newamount = oldval + amount;
				byte[] keyb = SS.tobytes(key);
				Fileutil.modifylastvalue2byvalue1(keyb, SS.tobytes(String.valueOf(newamount)),
						datafile(key, bigfilehash, datafolder(namespace, table, column)));
				return newamount;
			}
		}
	}

	public static Path datafile(String key, int bigfilehash, Path datafolder) {
		return datafolder.resolve(String.valueOf(Math.abs(key.hashCode()) % bigfilehash));
	}

	public static Path datafolder(String namespace, String table, String column) {
		if (namespace.trim().isEmpty() || table.trim().isEmpty() || column.trim().isEmpty()) {
			return null;
		} else {
			return SS.LOCAL_DATAFOLDER.resolve(namespace).resolve(table).resolve(column);
		}
	}

	public static Path indexfile(String index, Long pagenum, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {
		return indexfolder.resolve(SS.filtersandpagenum(pagenum, filters))
				.resolve(String.valueOf(Math.abs(index.hashCode()) % bigfilehash));
	}

	public static Path configfile(String namespace, String configfile) {
		return SS.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
	}

}
