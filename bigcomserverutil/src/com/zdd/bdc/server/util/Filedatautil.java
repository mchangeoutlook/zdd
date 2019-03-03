package com.zdd.bdc.server.util;

import java.nio.file.Path;

import com.zdd.bdc.client.util.STATIC;

public class Filedatautil {

	public static String read(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		return Filekvutil.readvaluebykey(STATIC.keylength, key,
				file(key, bigfilehash, folder(namespace, table, column)));
	}

	public static void delete(String key, String namespace, String table, String column, int bigfilehash)
			throws Exception {
		Filekvutil.deletevaluebykey(STATIC.keylength, key,
				file(key, bigfilehash, folder(namespace, table, column)));
	}

	public static void modify(String key, String newvalue, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		Filekvutil.modifyvaluebykey(STATIC.keylength, key, newvalue,
				file(key, bigfilehash, folder(namespace, table, column)));
	}

	public static void create(String key, String value, int extravaluecapacity, String namespace, String table,
			String column, int bigfilehash) throws Exception {
		synchronized (Fileutil.synckey(key)) {
			if (Filekvutil.readvaluebykey(STATIC.keylength, key, file(key, bigfilehash, folder(namespace, table, column)))!=null) {
				throw new Exception(STATIC.DUPLICATE);
			}
			Filekvutil.create(STATIC.keylength, key, value, extravaluecapacity,
					file(key, bigfilehash, folder(namespace, table, column)));
		}
	}

	public static long increment(String key, long amount, String namespace, String table, String column,
			int bigfilehash) throws Exception {
		synchronized (Fileutil.synckey(key)) {
			String amountstr = read(key, namespace, table, column, bigfilehash);
			if (amountstr == null) {
				Filekvutil.create(STATIC.keylength, key, String.valueOf(amount),
						String.valueOf(Long.MAX_VALUE).length() + 1,
						file(key, bigfilehash, folder(namespace, table, column)));
				return amount;
			} else {
				Long oldval = null;
				try {
					oldval = Long.parseLong(amountstr);
				} catch (Exception e) {
					throw new Exception("nolongvalue");
				}
				long newamount = oldval + amount;
				Filekvutil.modifyvaluebykey(STATIC.keylength, key, String.valueOf(newamount),
						file(key, bigfilehash, folder(namespace, table, column)));
				return newamount;
			}
		}
	}
	public static Path file(String key, int bigfilehash, Path datafolder) {
		return datafolder.resolve(String.valueOf(Math.abs(key.hashCode()) % bigfilehash));
	}

	public static Path folder(String namespace, String table, String column) {
		if (namespace.trim().isEmpty() || table.trim().isEmpty() || column.trim().isEmpty()) {
			return null;
		} else {
			return STATIC.LOCAL_DATAFOLDER.resolve(namespace).resolve(table).resolve(column);
		}
	}
}
