package com.zdd.bdc.biz;

import java.util.Calendar;
import java.util.Hashtable;
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
	
	public Textclient columns(int numofcolumns) throws Exception {
		if (numofcolumns==0) {
			throw new Exception("cols0");
		}
		cols = new Vector<String>(numofcolumns);
		return this;
	}

	public Textclient columnvalues(int numofcolumnvalues) throws Exception {
		if (numofcolumnvalues==0) {
			throw new Exception("cols0");
		}
		cvs = new Hashtable<String, String>(numofcolumnvalues);
		cvmaxs = new Hashtable<String, Integer>(numofcolumnvalues);
		return this;
	}
	
	public Textclient add(String column, String value, int max) throws Exception {
		if (column.isEmpty()) {
			throw new Exception("emptycol");
		}
		if (max==0) {
			throw new Exception("max0");	
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
		if (ns==null||tb==null||cvs.isEmpty()||cvmaxs.isEmpty()) {
			throw new Exception(".columnvalues.add.create");
		}
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
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String,String> read() throws Exception {
		if (key==null||ns==null||tb==null||cols.isEmpty()) {
			throw new Exception(".key.columns.add.read");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "read");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cols", cols);
			return (Map<String,String>)Objectutil.convert(Theclient.request("192.168.3.56", 9999, Objectutil.convert(params), null));
		}finally {
			clear();
		}
	}

	private void clear() {
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
