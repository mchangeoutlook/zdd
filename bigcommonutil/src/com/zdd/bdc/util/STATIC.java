package com.zdd.bdc.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class STATIC {
	public static final String SPLIT_IP_PORT = ":";
	public static final String SPLIT_VAL_VAL = "#";
	public static final String SPLIT_KEY_VAL = "#";
	public static final String SPLIT_A_B = "#";
	
	public static final String REMOTE_CONFIGFILE_BIGDATA = "bigdata";
	public static final String REMOTE_CONFIGFILE_BIGINDEX = "bigindex";
	public static final String REMOTE_CONFIGFILE_CORE = "core";
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
	public static final String PARAM_FILTERS_KEY = "filters";
	public static final String PARAM_TABLE_KEY = "tb";
	public static final String PARAM_COLUMNS_KEY = "cols";
	public static final String PARAM_COLUMNVALUES_KEY = "cvs";
	public static final String PARAM_COLUMNAMOUNTS_KEY = "cas";
	public static final String PARAM_COLUMNMAXVALUES_KEY = "cvmaxs";

	public static final String PARAM_DATA_KEY = "data";
	
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
	
	public static final SimpleDateFormat FORMAT_yMd = new SimpleDateFormat("yyyy-MM-dd");
	public static final String FORMAT_yMd(String key) {
		return key.substring(13,17)+"-"+key.substring(7,9)+"-"+key.substring(2,4);
	}
	public static final String FORMAT_KEY(String idcontainsmorethanequal9chars) {
		Calendar cal = Calendar.getInstance();
		return new StringBuffer(idcontainsmorethanequal9chars)
				.insert(2, String.format("%02d", cal.get(Calendar.DATE)))
				.insert(7, String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.insert(13, String.valueOf(cal.get(Calendar.YEAR))).toString();
	}
	
}
