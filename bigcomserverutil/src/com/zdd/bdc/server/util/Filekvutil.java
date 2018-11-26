package com.zdd.bdc.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Filekvutil {

	public static void configcreate(String key, String value, String namespace, String configfile) throws Exception {
		Fileutil.create(STATIC.tobytes(key), 100, STATIC.tobytes(value), 500, configfile(namespace, configfile));
	}
	public static void configmodify(String key, String newvalue, String namespace, String configfile) throws Exception {
		Fileutil.modifylastvalue2byvalue1(STATIC.tobytes(key), STATIC.tobytes(newvalue),
				configfile(namespace, configfile));
	}
	public static String config(String key, String namespace, String configfile) throws Exception {
		byte[] val = Fileutil.readfirstvalue2byvalue1(STATIC.tobytes(key), configfile(namespace, configfile));
		if (STATIC.NAMESPACE_CORE.equals(namespace)&&STATIC.REMOTE_CONFIG_PENDING.equals(configfile)) {
			configmodify(key,"",namespace,configfile);
		} else {
			//do nothing
		}
		if (val == null) {
			return null;
		} else {
			return STATIC.tostring(val);
		}
	}

	public static Vector<String> configs(String key, String namespace, String configfile) throws Exception {
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(STATIC.tobytes(key), 10000, true,
				configfile(namespace, configfile));
		Vector<String> returnvalue = new Vector<String>(vals.size());
		for (byte[] val : vals) {
			returnvalue.add(STATIC.tostring(val));
		}
		return returnvalue;
	}

	public static String indexversion(Path target) throws Exception {
		Vector<byte[]> val = Fileutil.readfirstvalue1value2(target);
		if (val.isEmpty()) {
			return null;
		} else {
			if (STATIC.VERSION_KEY.equals(STATIC.tostring(val.get(0)))) {
				return STATIC.tostring(val.get(1));
			} else {
				return null;
			}
		}
	}

	public static void indexversion(String version, String index, String value, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {

		Path target = indexfile(index, filters, bigfilehash, indexfolder);

		synchronized (STATIC.syncfile(target)) {
			if (!Files.exists(target) || Files.size(target) == 0) {
				Fileutil.create(STATIC.tobytes(STATIC.VERSION_KEY), STATIC.VERSION_KEY.length(),
						STATIC.tobytes(version), 100, target);
			} else {
				String existingversion = indexversion(target);
				if (existingversion == null) {
					throw new Exception("noexistversion");
				} else if (existingversion.compareTo(version) < 0) {
					Files.write(target, new byte[0], StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.SYNC);
					Fileutil.create(STATIC.tobytes(STATIC.VERSION_KEY), STATIC.VERSION_KEY.length(),
							STATIC.tobytes(version), 100, target);
				} else if (existingversion.compareTo(version) > 0) {
					throw new Exception("olderversion");
				} else {
					// do nothing
				}
			}
			index(index, filters, bigfilehash, indexfolder);
		}
	}

	public static String index(String index, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {

		Path target = indexfile(index, filters, bigfilehash, indexfolder);

		byte[] val = Fileutil.readlastvalue2byvalue1(STATIC.tobytes(index), target);
		if (val == null) {
			return null;
		} else {
			return STATIC.tostring(val);
		}
	}

	public static Vector<String> indexes(String index, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		Path target = indexfile(index, filters, bigfilehash, indexfolder);
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(STATIC.tobytes(index), 1000, true, target);
		Vector<String> returnvalue = new Vector<String>(vals.size());
		for (byte[] val : vals) {
			returnvalue.add(STATIC.tostring(val));
		}
		return returnvalue;
	}

	public static void index(String index, String value, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {
		Path target = indexfile(index, filters, bigfilehash, indexfolder);
		byte[] indexb = STATIC.tobytes(index);
		byte[] valueb = STATIC.tobytes(value);
		synchronized (STATIC.synckey(index)) {
			if (index(index, filters, bigfilehash, indexfolder) == null) {
				Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
			} else {
				throw new Exception("duplicate");
			}
		}
	}

	public static void indexes(String index, String value, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		Path target = indexfile(index, filters, bigfilehash, indexfolder);
		byte[] indexb = STATIC.tobytes(index);
		byte[] valueb = STATIC.tobytes(value);
		Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
	}

	public static Long indexincrement(String index, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {
		String key = index + STATIC.splitenc(filters);
		synchronized (STATIC.synckey(key)) {
			byte[] val = Fileutil.readlastvalue2byvalue1(STATIC.tobytes(key),
					indexfile(key, filters, bigfilehash, indexfolder));
			long amount = 0;
			if (val == null) {
				amount = 1;
			} else {
				amount = Long.parseLong(STATIC.tostring(val)) + 1;
			}

			byte[] keyb = STATIC.tobytes(key);

			if (val == null) {
				Fileutil.create(keyb, keyb.length, STATIC.tobytes(String.valueOf(amount)),
						String.valueOf(Long.MAX_VALUE).length() + 1, indexfile(key, filters, bigfilehash, indexfolder));
			} else {
				Fileutil.modifylastvalue2byvalue1(keyb, STATIC.tobytes(String.valueOf(amount)),
						indexfile(key, filters, bigfilehash, indexfolder));
			}
			return amount;
		}
	}

	public static String dataread(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		byte[] val = Fileutil.readlastvalue2byvalue1(STATIC.tobytes(key),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
		if (val == null) {
			return null;
		} else {
			return STATIC.tostring(val);
		}
	}

	public static void datadelete(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		Fileutil.deletelastvalue2byvalue1(STATIC.tobytes(key),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datamodify(String key, String newvalue, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		Fileutil.modifylastvalue2byvalue1(STATIC.tobytes(key), STATIC.tobytes(newvalue),
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datacreate(String key, String value, int valuemaxlength, String namespace, String table,
			String column, int bigfilehash) throws Exception {
		byte[] keyb = STATIC.tobytes(key);
		Fileutil.create(keyb, keyb.length, STATIC.tobytes(value), valuemaxlength,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static long dataincrement(String key, long amount, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		synchronized (STATIC.synckey(key)) {
			String amountstr = dataread(key, namespace, table, column, bigfilehash);
			if (amountstr == null) {
				byte[] keyb = STATIC.tobytes(key);
				Fileutil.create(keyb, keyb.length, STATIC.tobytes(String.valueOf(amount)),
						String.valueOf(Long.MAX_VALUE).length() + 1,
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
				byte[] keyb = STATIC.tobytes(key);
				Fileutil.modifylastvalue2byvalue1(keyb, STATIC.tobytes(String.valueOf(newamount)),
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
			return STATIC.LOCAL_DATAFOLDER.resolve(namespace).resolve(table).resolve(column);
		}
	}
	
	public static Path indexfolder(String namespace, Vector<String> filters, String index, int maxindexservers) {
		return STATIC.LOCAL_DATAFOLDER.resolve(namespace)
				.resolve(STATIC.distributebigindexserveri(namespace, filters, index, maxindexservers));
	}
	
	public static Path indexfile(String index, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {
		String fs = STATIC.splitenc(filters);
		return indexfolder.resolve(fs).resolve(String.valueOf(Math.abs((fs+index).hashCode()) % bigfilehash));
	}

	public static Path configfile(String namespace, String configfile) {
		return STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
	}

}
