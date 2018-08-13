package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;
import com.zdd.bdc.util.STATIC;

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
			key = Bigclient.newbigdatakey();
			Map<String, Object> params = new Hashtable<String, Object>(6);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNVALUES_KEY, cvs);
			params.put(STATIC.PARAM_COLUMNMAXVALUES_KEY, cvmaxs);
			String[] iport = Bigclient.distributebigdata(ns, key).split(STATIC.SPLIT_IP_PORT);
			Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
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
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_DELETE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, key).split(STATIC.SPLIT_IP_PORT);
			Objectutil.convert(Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
		} finally {
			clear();
		}
	}
	

	public String modify() throws Exception {
		if (key == null || cvs.isEmpty()) {
			throw new Exception(".key.columnvalues.add4modify.modify");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_MODIFY);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNVALUES_KEY, cvs);
			String[] iport = Bigclient.distributebigdata(ns, key).split(STATIC.SPLIT_IP_PORT);
			Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
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
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, key).split(STATIC.SPLIT_IP_PORT);
			return (Map<String, String>) Objectutil
					.convert(Theclient.request(iport[0], 
							Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
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
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_INCREMENT);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNAMOUNTS_KEY, cas);
			String[] iport = Bigclient.distributebigdata(ns, key).split(STATIC.SPLIT_IP_PORT);
			
			return (Map<String, Long>) Objectutil
					.convert(Theclient.request(iport[0], 
							Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
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

}
