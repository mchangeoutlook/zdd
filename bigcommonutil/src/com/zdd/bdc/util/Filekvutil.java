package com.zdd.bdc.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class Filekvutil {
	private static final int configkeymaxlength = 100;
	private static final int configvalmaxlength = 500;
	private static final int configmaxnumofvals = 100;
	
	private static final String versionkey = ":version:";
	private static final int versionkeymaxlength = 9;
	private static final int versionvalmaxlength = 50;
	
	private static final int incrementmaxlength = String.valueOf(Long.MAX_VALUE).length();
	
	public static void config(String key, String value, String namespace, String configfile) throws Exception {
		Path target = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
		Fileutil.create(key.getBytes(STATIC.CHARSET_DEFAULT), configkeymaxlength, value.getBytes(STATIC.CHARSET_DEFAULT), configvalmaxlength, target);
	}
	
	public static String config(String key, String namespace, String configfile) throws Exception {
		Path target = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
		byte[] val = Fileutil.readfirstvalue2byvalue1(key.getBytes(STATIC.CHARSET_DEFAULT), target);
		if (val==null) {
			return null;
		} else {
			return new String(val, STATIC.CHARSET_DEFAULT);
		}
	}
	
	public static String[] configs(String key, String namespace, String configfile) throws Exception {
		Path target = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(key.getBytes(STATIC.CHARSET_DEFAULT), configmaxnumofvals, true, target);
		String[] returnvalue = new String[vals.size()];
		for (int i = 0;i<vals.size();i++) {
			returnvalue[i] = new String(vals.get(i), STATIC.CHARSET_DEFAULT);
		}
		return returnvalue;
	}

	public static String indexversion(Path target) throws Exception {
		Vector<byte[]> val = Fileutil.readfirstvalue1value2(target);
		if (val.isEmpty()) {
			return null;
		} else {
			if (versionkey.equals(new String(val.get(0),STATIC.CHARSET_DEFAULT))) {
				return new String(val.get(1), STATIC.CHARSET_DEFAULT);
			} else {
				return null;
			}
		}
	}

	public static void indexversion(String version, Path target) throws Exception {
		synchronized(Integer.valueOf(Math.abs(target.getFileName().toString().hashCode()))) {
			if (!Files.exists(target)||Files.size(target)==0) {
				Fileutil.create(versionkey.getBytes(STATIC.CHARSET_DEFAULT), versionkeymaxlength, version.getBytes(STATIC.CHARSET_DEFAULT), versionvalmaxlength, target);
			} else {
				String existingversion = indexversion(target);
				if (existingversion==null) {
					throw new Exception("noexistversion");
				} else {
					Fileutil.modifyfirstvalue2byvalue1(versionkey.getBytes(STATIC.CHARSET_DEFAULT), version.getBytes(STATIC.CHARSET_DEFAULT), target);
				}
			}
		}
	}
	
	public static String index(String index, Path target) throws Exception {
		byte[] val = Fileutil.readlastvalue2byvalue1(index.getBytes(STATIC.CHARSET_DEFAULT), target);
		if (val == null) {
			return null;
		} else {
			return new String(val, STATIC.CHARSET_DEFAULT);
		}
	}

	public static String[] indexes(String index, int numofvals, Path target) throws Exception {
		Vector<byte[]> vals = Fileutil.readallvalue2byvalue1(index.getBytes(STATIC.CHARSET_DEFAULT), numofvals, true, target);
		String[] returnvalue = new String[vals.size()];
		for (int i=0;i<vals.size();i++) {
			returnvalue[i] = new String(vals.get(i), STATIC.CHARSET_DEFAULT);
		}
		return returnvalue;
	}

	public static void index(String index, String value, boolean isunique, Path target) throws Exception {
		if (isunique) {
			synchronized(Integer.valueOf(Math.abs(target.getFileName().toString().hashCode()))) {
				if (index(index, target)==null) {
					byte[] indexb = index.getBytes(STATIC.CHARSET_DEFAULT);
					byte[] valueb = value.getBytes(STATIC.CHARSET_DEFAULT);
					Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
				} else {
					throw new Exception("duplicate");
				}
			}
		} else {
			byte[] indexb = index.getBytes(STATIC.CHARSET_DEFAULT);
			byte[] valueb = value.getBytes(STATIC.CHARSET_DEFAULT);
			Fileutil.create(indexb, indexb.length, valueb, valueb.length, target);
		}
		
	}
	
	public static String dataread(String key, Path target) throws Exception {
		byte[] val = Fileutil.readlastvalue2byvalue1(key.getBytes(STATIC.CHARSET_DEFAULT), target);
		if (val==null) {
			return null;
		} else {
			return new String(val, STATIC.CHARSET_DEFAULT);
		}
	}

	public static void datadelete(String key, Path target) throws Exception {
		Fileutil.deletelastvalue2byvalue1(key.getBytes(STATIC.CHARSET_DEFAULT), target);
	}

	public static void datamodify(String key, String newvalue, Path target) throws Exception {
		Fileutil.modifylastvalue2byvalue1(key.getBytes(STATIC.CHARSET_DEFAULT), newvalue.getBytes(STATIC.CHARSET_DEFAULT), target);
	}

	public static void datacreate(String key, String value, int valuemaxlength, Path target) throws Exception {
		byte[] keyb = key.getBytes(STATIC.CHARSET_DEFAULT);
		Fileutil.create(keyb, keyb.length, value.getBytes(STATIC.CHARSET_DEFAULT), valuemaxlength, target);
	}

	public static long dataincrement(String key, long amount, Path target) throws Exception {
		synchronized(Integer.valueOf(Math.abs(target.getFileName().toString().hashCode()))) {
			String amountstr = dataread(key, target);
			if (amountstr==null) {
				byte[] keyb = key.getBytes(STATIC.CHARSET_DEFAULT);
				Fileutil.create(keyb, keyb.length, String.valueOf(amount).getBytes(STATIC.CHARSET_DEFAULT), incrementmaxlength+1, target);
				return amount;
			} else {
				Long oldval = null;
				try {
					oldval = Long.parseLong(amountstr);
				}catch(Exception e) {
					throw new Exception("nolongvalue");
				}
				long newamount = oldval+amount;
				byte[] keyb = key.getBytes(STATIC.CHARSET_DEFAULT);
				Fileutil.create(keyb, keyb.length, String.valueOf(newamount).getBytes(STATIC.CHARSET_DEFAULT), incrementmaxlength+1, target);
				return newamount;
			}
		}
	}
	
}
