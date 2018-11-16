package com.zdd.bdc.server.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class SS {
	public static final Path LOCAL_CONFIGFOLDER = Paths.get("config");
	public static final Path LOCAL_DATAFOLDER = Paths.get(".").toAbsolutePath().getParent().getParent().resolve("data");
	public static final String PARENTFOLDER = Paths.get(".").toAbsolutePath().getParent().getParent().getFileName()
			.toString();

	public static final int configkeymaxlength = 100;
	public static final int configvalmaxlength = 500;
	public static final int configmaxnumofvals = 100;

	public static final String versionkey = ":version:";
	public static final int versionkeymaxlength = 9;
	public static final int versionvalmaxlength = 50;

	public static final int incrementmaxlength = String.valueOf(Long.MAX_VALUE).length() + 1;
	
	public static final String SORT_COMPARE_TO_STRING = "0";
	public static final String SORT_ALL_FOLDER = "#";
	public static final String SORT_SEQUENCE(boolean isasc) {
		if (isasc) {
			return "ascending";
		} else {
			return "descending";
		}
	}
	public static final boolean SORT_SEQUENCE(String sequence) {
		if ("ascending".equals(sequence)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static final String REMOTE_CONFIG_DIG = "dig";
	public static final String REMOTE_CONFIG_PENDING = "pending";

	public static final String REMOTE_CONFIGVAL_PENDING = "pending";

	public static final SimpleDateFormat FORMAT_yMd = new SimpleDateFormat("yyyyMMdd");
	
	// to avoid dead lock, sync file and key on different range.
	private static final int synchash = 10000;

	public static String syncfile(Path tosync) {

		return String.valueOf(Math.abs(tosync.getFileName().toString().hashCode() % synchash) + synchash).intern();
	}

	public static String synckey(String tosync) {
		return String.valueOf(Math.abs(tosync.hashCode() % synchash)).intern();
	}

	public static byte[] tobytes(String str) throws Exception {
		return str.getBytes("UTF-8");
	}

	public static String tostring(byte[] b) throws Exception {
		return new String(b, "UTF-8");
	}

	public static String urldecode(String str) throws Exception {
		return URLDecoder.decode(str, "UTF-8");
	}

	public static String urlencode(String str) throws Exception {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	public static String[] splitfromto(String str) {
		return str.split("-");
	}
	public static String splitfromto(String from, String to) {
		return from+"-"+to;
	}

	public static String filtersandpagenum(Long pagenum, Vector<String> filters) throws Exception {
		String filtersandpagenum = "";
		if (filters != null && !filters.isEmpty()) {
			for (String f : filters) {
				if (!f.trim().isEmpty()) {
					filtersandpagenum += SS.urlencode(f) + "#";
				} else {
					throw new Exception("hasemptyfilter");
				}
			}
		}
		if (filtersandpagenum.isEmpty()) {
			filtersandpagenum += "#" + (pagenum==null?"-1":String.valueOf(pagenum));
		} else {
			filtersandpagenum += (pagenum==null?"-1":String.valueOf(pagenum));
		}
		return filtersandpagenum;
	}

	public static Vector<String> filtersandpagenum(String filtersandpagenum) throws Exception {
		String[] splits = filtersandpagenum.split("#");
		Vector<String> returnvalue = new Vector<String>(splits.length);
		for (String s : splits) {
			returnvalue.add(urldecode(s));
		}
		return returnvalue;
	}

}
