package com.zdd.bdc.biz;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Textclient {
	public static String create() throws Exception {
		Map<String, List<Map<String, Object>>> a = new HashMap<String, List<Map<String, Object>>>();
		Map<String, Object> aa = new HashMap<String, Object>();
		aa.put("222", 2);
		aa.put("333", "3");
		List<Map<String, Object>> aaa = new ArrayList<Map<String, Object>>();
		aaa.add(aa);
		a.put("111", aaa);
		byte[] res = Theclient.request("127.0.0.1", 9999, Objectutil.convert(a), null);
		System.out.println(res.length);
		
		Map<String, Object> b = (Map<String, Object>)Objectutil.convert(res);
		List<Object> c = (List<Object>)b.get("111");
		System.out.println(((Map<String, Object>)c.get(0)).get("222"));
		System.out.println(((Map<String, Object>)c.get(0)).get("333"));
		return "";
	}
	public static void delete() {
		
	}
	public static void modify() {
		
	}
	public static String search() {
		return null;
	}
	public static long increment() {
		return 0l;
	}
}
