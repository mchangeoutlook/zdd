package com.tenotenm.yanxin.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

public class Reuse {
	
	public static final String namespace_yanxin = "yanxin";
	public static final String namespace_bigfilefrom = "bigfilefrom";
	public static final String namespace_bigfileto = "bigfileto";
	
	public static final String msg_hint="提示: ";
	
	public static final String NOTFOUND = "YX_NOTFOUND";
	
	private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	private static final String yyyyMMdd = "yyyy-MM-dd";

	public static String yyyyMMddHHmmss(Date date) {
		return new SimpleDateFormat(yyyyMMddHHmmss).format(date);
	}

	public static Date yyyyMMddHHmmss(String datestr) {
		try {
			return new SimpleDateFormat(yyyyMMddHHmmss).parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}

	public static String yyyyMMdd(Date date) {
		return new SimpleDateFormat(yyyyMMdd).format(date);
	}

	public static Date yyyyMMdd(String datestr) {
		try {
			return new SimpleDateFormat(yyyyMMdd).parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}

	private static final String escapehtml(String str) {
		if (str != null) {
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
		}
		return str;
	}

	public static void respond(HttpServletResponse response, Object returnvalue, Throwable t) throws IOException {
		try {
			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("state", "success");
			if (returnvalue != null) {
				ret.put("data", returnvalue);
			} else {
				ret.put("data", "");
			}
			if (t != null) {
				ret.put("state", "fail");
				StringWriter errors = new StringWriter();
				t.printStackTrace(new PrintWriter(errors));
				if (t.getMessage()!=null&&t.getMessage().startsWith(msg_hint)) {
					ret.put("error", t.getMessage());
				} else {
					ret.put("error", "系统繁忙，请稍后再来");
				}
				ret.put("detail", errors.toString());
			}
			response.setContentType("application/json");
			response.getWriter().print(escapehtml(new ObjectMapper().writeValueAsString(ret)));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static String getremoteip(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip.split(",")[0];
	}

	public static String getuseragent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}
	
	public static long getdaysmillisconfig(String configkey) {
		return Long.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
				.read(configkey)) * 24 * 60 * 60 * 1000;
	}
	
	public static long getsecondsmillisconfig(String configkey) {
		return Long.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
				.read(configkey)) * 1000;
	}

	public static long getlongvalueconfig(String configkey) {
		return Long.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
				.read(configkey));
	}
	
	
	public static String sign(String raw) {
		try {
			MessageDigest crypt = MessageDigest.getInstance("MD5");
			crypt.reset();
			crypt.update(raw.getBytes("UTF-8"));
			return byteArrayToHexString(crypt.digest());
		} catch (Exception e) {
			return null;
		}

	}

	private static String[] HexCode = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
			"f" };

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return HexCode[d1] + HexCode[d2];
	}

	private static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result = result + byteToHexString(b[i]);
		}
		return result;
	}
	
}
