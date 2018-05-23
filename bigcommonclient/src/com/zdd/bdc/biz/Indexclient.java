package com.zdd.bdc.biz;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Indexclient {
	
	private String key = null;
	private String ns = null;
	private String tb = null;
	private Map<String, String> cvs = null;
	private Map<String, Integer> cvmaxs = null;
	private Vector<String> cols = null;
	
	private Indexclient(String namespace, String table) {
		ns = namespace;
		tb = table;
	}
	
	public static Indexclient getinstance(String namespace, String table) {
		return new Indexclient(namespace, table);
	}
	
	public Indexclient key(String existkey) {
		key = existkey;
		return this;
	}
	
	public Indexclient columns(int numofcolumns) {
		cols = new Vector<String>(numofcolumns);
		return this;
	}

	public Indexclient columnvalues(int numofcolumnvalues) {
		cvs = new Hashtable<String, String>(numofcolumnvalues);
		cvmaxs = new Hashtable<String, Integer>(numofcolumnvalues);
		return this;
	}
	
	public Indexclient add(String column, String value, int max) throws Exception {
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
	
	public Indexclient add(String column) throws Exception {
		if (column.isEmpty()) {
			throw new Exception("emptycol");
		}
		cols.add(column);
		return this;
	}
	
	public String create() throws Exception {
		try {
			key = newkey();
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "create");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cvs", cvs);
			params.put("cvmaxs", cvmaxs);
			Theclient.request("192.168.3.56", 9999, Objectutil.convert(params), null);
			return key;
		}finally {
			key = null;
			ns = null;
			tb = null;
			if (cvs!=null) {
				cvs.clear();
			}
			cvs = null;
			if (cvmaxs!=null) {
				cvmaxs.clear();
			}
			cvmaxs = null;
			if (cols!=null) {
				cols.clear();
			}
			cols = null;
		}
	}

	public void delete() throws Exception {
		if (key.getBytes("UTF-8").length!=40) {
			throw new Exception("key40");
		}
	}

	public void modify() throws Exception {
		if (key.getBytes("UTF-8").length!=40) {
			throw new Exception("key40");
		}
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
