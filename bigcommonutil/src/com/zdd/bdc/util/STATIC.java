package com.zdd.bdc.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class STATIC {
	
	public static final String CHARSET_DEFAULT = "UTF-8";
	
	public static final String SORT_COMPARE_TO_STRING = "0";
	public static final int SORT_PROGRESSONEFILECAPACITY = 800000;
	public static final String SORT_ALL = "ALLwithoutconsideringanyfilter";
	public static final String SORT_ROOTFOLDER = "sorting";
	public static final String SORT_DONEFILE = "done";
	
	public static final String REMOTE_CONFIGFILE_BIGDATA = "bigdata";
	public static final String REMOTE_CONFIGFILE_BIGINDEX = "bigindex";
	public static final String REMOTE_CONFIGFILE_CORE = "core";
	public static final String REMOTE_CONFIGFILE_DIG = "dig";
	public static final String REMOTE_CONFIGFILE_PENDING = "pending";

	public static final String REMOTE_CONFIGKEY_CONFIGSERVERIP = "configserverip";
	public static final String REMOTE_CONFIGKEY_CONFIGSERVERPORT = "configserverport";
	public static final String REMOTE_CONFIGKEY_MAXINDEXSERVERS = "maxindexservers";
	public static final String REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS = "updateconfigcache.intervalseconds";
	
	public static final String REMOTE_CONFIGVAL_PENDING = "pending";
	
	public static final int PAGENUM_UNIQUEINDEX= -1;

	public static final String PARAM_KEY_KEY = "key";
	public static final String PARAM_NAMESPACE_KEY = "ns";
	public static final String PARAM_INDEX_KEY = "index";
	public static final String PARAM_PAGENUM_KEY = "pagenum";
	public static final String PARAM_NUMOFDATA = "numofdata";
	public static final String PARAM_FILTERS_KEY = "filters";
	public static final String PARAM_TABLE_KEY = "tb";
	public static final String PARAM_COLUMNS_KEY = "cols";
	public static final String PARAM_COLUMNVALUES_KEY = "cvs";
	public static final String PARAM_COLUMNAMOUNTS_KEY = "cas";
	public static final String PARAM_COLUMNMAXVALUES_KEY = "cvmaxs";

	public static final String PARAM_DATA_KEY = "data";
	public static final String PARAM_VERSION_KEY = "version";
	
	public static final String PARAM_ACTION_KEY = "action";
	public static final String PARAM_ACTION_READ = "read";
	public static final String PARAM_ACTION_DELETE = "delete";
	public static final String PARAM_ACTION_MODIFY = "modify";
	public static final String PARAM_ACTION_CREATE = "create";
	public static final String PARAM_ACTION_INCREMENT = "increment";

	public static final String NAMESPACE_CORE = "core";
	
	public static final Path LOCAL_CONFIGFOLDER = Paths.get("config");
	public static final Path LOCAL_DATAFOLDER = Paths.get(".").toAbsolutePath().getParent().getParent().resolve("data");
	public static final String PARENTFOLDER = Paths.get(".").toAbsolutePath().getParent().getParent().getFileName().toString();
	
	public static final SimpleDateFormat FORMAT_yMd = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat FORMAT_yMdHms = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String FORMAT_yMd(String key) {
		return key.substring(13,17)+key.substring(7,9)+key.substring(2,4);
	}
	public static final String FORMAT_KEY(String idcontainsmorethanequal9chars) {
		Calendar cal = Calendar.getInstance();
		return new StringBuffer(idcontainsmorethanequal9chars)
				.insert(2, String.format("%02d", cal.get(Calendar.DATE)))
				.insert(7, String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.insert(13, String.valueOf(cal.get(Calendar.YEAR))).toString();
	}
	
	private static final String SPLIT_ENC = "#";
	public static final String[] splitenc(String from) throws Exception {
		String[] returnvalue = from.split(SPLIT_ENC);
		for (int i = 0;i < returnvalue.length;i++) {
			returnvalue[i] = URLDecoder.decode(returnvalue[i], CHARSET_DEFAULT);
		}
		return returnvalue;
	}
	public static final String splitenc(String... values) throws Exception {
		String returnvalue = "";
		for (String val:values) {
			if (!returnvalue.isEmpty()) {
				returnvalue+=SPLIT_ENC;
			}
			returnvalue+=URLEncoder.encode(val, CHARSET_DEFAULT);
		}
		return returnvalue;
	}

	private static final String SPLIT = ":";
	public static final String[] split(String from) {
		return from.split(SPLIT);
	}
	public static final String split(String... values) {
		String returnvalue = "";
		for (String val:values) {
			if (!returnvalue.isEmpty()) {
				returnvalue+=SPLIT;
			}
			returnvalue+=val;
		}
		return returnvalue;
	}
	
	private static final String SPLIT_KEY_VAL = "=";
	public static final String[] keyval(String from) throws Exception {
		String[] returnvalue = new String[2];
		returnvalue[0] = URLDecoder.decode(from.substring(0, from.indexOf(SPLIT_KEY_VAL)),CHARSET_DEFAULT);
		if (from.endsWith(SPLIT_KEY_VAL)) {
			returnvalue[1] = "";
		} else {
			returnvalue[1] = from.substring(from.indexOf(SPLIT_KEY_VAL)+1);
		}
		return returnvalue;
	}
	public static final String keyval(String key, String value) throws Exception {
		return URLEncoder.encode(key,CHARSET_DEFAULT)+SPLIT_KEY_VAL+value;
	}

	private static final String SPLIT_FROM_TO = "-";
	public static final String[] fromto(String fromto) throws Exception {
		return fromto.split(SPLIT_FROM_TO);
	}
	public static final String fromto(String from, String to) throws Exception {
		return from+SPLIT_FROM_TO+to;
	}

	private static final String SPLIT_COMMENT = "@";
	public static final String commentget(String line) {
		if (line!=null&&line.startsWith(SPLIT_COMMENT)) {
			if (line.endsWith(SPLIT_COMMENT)) {
				return "";
			} else {
				return line.substring(1);
			}
		} else {
			return null;
		}
	}
	public static final String commentput(String comment) {
		return SPLIT_COMMENT+comment;
	}
	
	public static void main(String[] s) throws Exception {
		Vector<String> a = new Vector<String>(3);
		a.add("12345");
		a.add("abcd");
		a.add("efgh");
		a.add("3456");
		a.add("bcde");
		a.add("2345");
		String[] aa = new String[a.size()];
		System.out.println(STATIC.split(a.toArray(aa)));
	}
	
}
