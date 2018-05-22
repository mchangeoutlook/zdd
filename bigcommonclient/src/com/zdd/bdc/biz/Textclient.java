package com.zdd.bdc.biz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Textclient {
	
	private String key = null;
	private String ns = null;
	private String tb = null;
	private Map<String, String> cvs = null;
	private Map<String, Integer> cvmaxs = null;
	private Vector<String> cols = null;
	
	private Textclient(String namespace, String table) {
		ns = namespace;
		tb = table;
	}
	
	public static Textclient getinstance(String namespace, String table) {
		return new Textclient(namespace, table);
	}
	
	public Textclient key(String existkey) {
		key = existkey;
		return this;
	}
	
	public Textclient columns(int numofcolumns) {
		cols = new Vector<String>(numofcolumns);
		return this;
	}

	public Textclient columnvalues(int numofcolumnvalues) {
		cvs = new Hashtable<String, String>(numofcolumnvalues);
		cvmaxs = new Hashtable<String, Integer>(numofcolumnvalues);
		return this;
	}
	
	public Textclient add(String column, String value, int max) throws Exception {
		if (column.isEmpty()) {
			throw new Exception("emptycol");
		}
		if (value.getBytes("UTF-8").length>max) {
			throw new Exception("val>max");
		}
		cvs.put(column, value);
		cvmaxs.put(column, max);
		return this;
	}
	
	public Textclient add(String column) throws Exception {
		if (column.isEmpty()) {
			throw new Exception("emptycol");
		}
		cols.add(column);
		return this;
	}
	
	public String create() throws Exception {
		key = newkey();
		Map<String, Object> params = new Hashtable<String, Object>(5);
		params.put("key", key);
		params.put("ns", ns);
		params.put("tb", tb);
		params.put("cvs", cvs);
		params.put("cvmaxs", cvmaxs);
		Theclient.request("192.168.3.56", 9999, Objectutil.convert(params), null);
		return key;
	}

	public void delete() throws Exception {
		if (key.getBytes("UTF-8").length!=40) {
			throw new Exception("key40");
		}
		Map<String, Object> params = new Hashtable<String, Object>(4);
		params.put("key", key);
		params.put("ns", ns);
		params.put("tb", tb);
		params.put("cols", cols);
		Theclient.request("192.168.3.56", 9999, Objectutil.convert(params), null);
	}

	public void modify() throws Exception {
		if (key.getBytes("UTF-8").length!=40) {
			throw new Exception("key40");
		}
		Map<String, Object> params = new Hashtable<String, Object>(4);
		params.put("key", key);
		params.put("ns", ns);
		params.put("tb", tb);
		params.put("cols", cols);
		Theclient.request("192.168.3.56", 9999, Objectutil.convert(params), null);
	}

	private static String newkey() {
		Calendar cal = Calendar.getInstance();
		String key = new StringBuffer(UUID.randomUUID().toString().replaceAll("-", ""))
				.insert(2, String.format("%02d", cal.get(Calendar.DATE)))
				.insert(7, String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.insert(13, String.valueOf(cal.get(Calendar.YEAR))).toString();
		return key;
	}
}
