package com.tenotenm.gdy.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Common {
	
	public static String generateid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static boolean isvalid(String id) {
		return id!=null&&id.trim().length()==32;
	}
	
	private static final String escapehtml(String str) {
		if (str!=null) {
			str=str.replaceAll("<", "&lt;");
			str=str.replaceAll(">", "&gt;");
		}
		return str;
	}
	public static String saygood(String str) {
		if (str.toLowerCase().contains("tmd")||str.toLowerCase().contains("fuck")||str.toLowerCase().contains("ass")||
				str.contains("操")||str.contains("傻")||str.contains("妈")&&!str.contains("妈妈")||str.contains("狗")||str.contains("狱")||
				str.contains("屎")||str.contains("蛋")||str.contains("猪")||str.contains("病")||str.contains("鸡")||str.contains("尿")||str.contains("死")) {
			return "多读书";
		}
		return str.trim();
	}
	public static void respond(HttpServletResponse response, Object returnvalue, Throwable t) throws IOException {
		try {
			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("state", "success");
			if (returnvalue!=null) {
				ret.put("data", returnvalue);
			} else {
				ret.put("data", "");
			}
			if (t!=null) {
				ret.put("state", "fail");
				StringWriter errors = new StringWriter();
				t.printStackTrace(new PrintWriter(errors));
				if (t.getMessage()!=null) {
					ret.put("error", t.getMessage());
				} else {
					ret.put("error", "NullPointerException");
				}
				ret.put("detail", errors.toString());
			}
			response.setContentType("application/json");
			response.getWriter().print(escapehtml(new ObjectMapper().writeValueAsString(ret)));
		}catch(Exception e) {
			throw new IOException(e);
		}
	}
	
}
