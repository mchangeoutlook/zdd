package com.zdd.bdc.server.util;

import java.nio.file.Path;

import com.zdd.bdc.client.util.STATIC;

public class Filedatautil {

	public static String dataread(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		return Filekvutil.readvaluebykey(key,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datadelete(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		Filekvutil.deletevaluebykey(key,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datamodify(String key, String newvalue, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		Filekvutil.modifyvaluebykey(key, newvalue,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static void datacreate(String key, String value, int extravaluecapacity, String namespace, String table,
			String column, int bigfilehash) throws Exception {
		Filekvutil.create(key, value, extravaluecapacity,
				datafile(key, bigfilehash, datafolder(namespace, table, column)));
	}

	public static long dataincrement(String key, long amount, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		synchronized (Fileutil.synckey(key)) {
			String amountstr = dataread(key, namespace, table, column, bigfilehash);
			if (amountstr == null) {
				Filekvutil.create(key, String.valueOf(amount),
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
				Filekvutil.modifyvaluebykey(key, String.valueOf(newamount),
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
}
