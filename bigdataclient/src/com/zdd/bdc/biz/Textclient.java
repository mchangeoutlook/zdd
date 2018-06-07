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
	private Map<String, Long> cas = null;
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

	public Textclient columnamounts(int numofcolumnamounts) {
		cas = new Hashtable<String, Long>(numofcolumnamounts);
		return this;
	}

	public Textclient add4create(String column, String value, int max) {
		cvs.put(column, value);
		cvmaxs.put(column, max);
		return this;
	}

	public Textclient add4increment(String column, long amount)  {
		cas.put(column, amount);
		return this;
	}

	public Textclient add4modify(String column, String value) {
		if (value == null) {
			value = "";
		}
		cvs.put(column, value);
		return this;
	}

	public Textclient add(String column) {
		cols.add(column);
		return this;
	}

	public String create() throws Exception {
		if (cvs.isEmpty() || cvmaxs.isEmpty()) {
			throw new Exception(".columnvalues.add4create.create");
		}
		try {
			key = newkey();
			Map<String, Object> params = new Hashtable<String, Object>(6);
			params.put("key", key);
			params.put("action", "create");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cvs", cvs);
			params.put("cvmaxs", cvmaxs);
			Theclient.request(distribute(ns, key), 
					Integer.parseInt(Configclient.getinstance("core", "core").read("textserverport")), Objectutil.convert(params), null);
			return key;
		} finally {
			clear();
		}
	}

	public void delete() throws Exception {
		if (key == null || cols.isEmpty()) {
			throw new Exception(".key.columns.add.delete");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "delete");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cols", cols);
			Objectutil.convert(Theclient.request(distribute(ns, key), 
					Integer.parseInt(Configclient.getinstance("core", "core").read("textserverport")), Objectutil.convert(params), null));
		} finally {
			clear();
		}
	}
	
	private static String distribute(String namespace, String key) {
		String configkey = key.substring(13,17)+"-"+key.substring(7,9)+"-"+key.substring(2,4);
		String[] ips = Configclient.getinstance(namespace, "bigdata").read(configkey).split("#");
		String ip = ips[Math.abs(key.hashCode())%ips.length];
		return ip;
	}

	public String modify() throws Exception {
		if (key == null || cvs.isEmpty()) {
			throw new Exception(".key.columnvalues.add4modify.modify");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "modify");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cvs", cvs);
			Theclient.request(distribute(ns, key), 
					Integer.parseInt(Configclient.getinstance("core", "core").read("textserverport")), Objectutil.convert(params), null);
			return key;
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> read() throws Exception {
		if (key == null || cols.isEmpty()) {
			throw new Exception(".key.columns.add.read");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "read");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cols", cols);
			return (Map<String, String>) Objectutil
					.convert(Theclient.request(distribute(ns, key), 
							Integer.parseInt(Configclient.getinstance("core", "core").read("textserverport")), Objectutil.convert(params), null));
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> increment() throws Exception {
		if (key == null || cas.isEmpty()) {
			throw new Exception(".key.columns.add4increment.increment");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("key", key);
			params.put("action", "increment");
			params.put("ns", ns);
			params.put("tb", tb);
			params.put("cas", cas);
			return (Map<String, Long>) Objectutil
					.convert(Theclient.request(distribute(ns, key), 
							Integer.parseInt(Configclient.getinstance("core", "core").read("textserverport")), Objectutil.convert(params), null));
		} finally {
			clear();
		}
	}

	private void clear() {
		key = null;
		ns = null;
		tb = null;
		if (cvs != null) {
			cvs.clear();
		}
		cvs = null;
		if (cvmaxs != null) {
			cvmaxs.clear();
		}
		cvmaxs = null;
		if (cols != null) {
			cols.clear();
		}
		cols = null;
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
