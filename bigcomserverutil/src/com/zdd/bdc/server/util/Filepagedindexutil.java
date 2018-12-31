package com.zdd.bdc.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Filepagedindexutil {

	public static String indexversion(Path target) throws Exception {
		Vector<String> val = Filekvutil.readfirstkeyvalue(target);
		if (val.isEmpty()) {
			return null;
		} else {
			if (STATIC.VERSION_KEY.equals(val.get(0))) {
				return val.get(1);
			} else {
				return null;
			}
		}
	}

	public static void indexversion(String version, String index, String value, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {

		Path target = indexfile(index, filters, bigfilehash, indexfolder);

		synchronized (Fileutil.syncfile(target)) {
			if (!Files.exists(target) || Files.size(target) == 0) {
				Filekvutil.create(STATIC.VERSION_KEY,
						version, 10, target);
			} else {
				String existingversion = indexversion(target);
				if (existingversion == null) {
					throw new Exception("noexistversion");
				} else if (existingversion.compareTo(version) < 0) {
					Files.write(target, new byte[0], StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.SYNC);
					Filekvutil.create(STATIC.VERSION_KEY,
							version, 10, target);
				} else if (existingversion.compareTo(version) > 0) {
					throw new Exception("olderversion");
				} else {
					// do nothing
				}
			}
			indexes(index, value, filters, bigfilehash, indexfolder);
		}
	}

	public static Vector<String> indexes(String index, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		Path target = indexfile(index, filters, bigfilehash, indexfolder);
		Vector<String> returnvalue = Filekvutil.readallvaluesbykey(index, target);
		return returnvalue;
	}
	public static void indexes(String index, String value, Vector<String> filters, int bigfilehash,
			Path indexfolder) throws Exception {
		Path target = indexfile(index, filters, bigfilehash, indexfolder);
		Filekvutil.create(index, value, 10, target);
	}

	public static Long indexincrement(String index, Vector<String> filters, int bigfilehash, Path indexfolder)
			throws Exception {
		String key = index + STATIC.splitenc(filters);
		synchronized (Fileutil.synckey(key)) {
			String val = Filekvutil.readvaluebykey(key,
					indexfile(key, filters, bigfilehash, indexfolder));
			long amount = 0;
			if (val == null) {
				amount = 1;
			} else {
				amount = Long.parseLong(val) + 1;
			}

			if (val == null) {
				Filekvutil.create(key, String.valueOf(amount),
						String.valueOf(Long.MAX_VALUE).length() + 1, indexfile(key, filters, bigfilehash, indexfolder));
			} else {
				Filekvutil.modifyvaluebykey(key, String.valueOf(amount),
						indexfile(key, filters, bigfilehash, indexfolder));
			}
			return amount;
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

}
