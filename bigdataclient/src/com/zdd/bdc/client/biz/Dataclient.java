package com.zdd.bdc.client.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;

public class Dataclient {

	private String key = null;
	private String ns = null;
	private String tb = null;
	private Map<String, String> cvs = null;
	private Map<String, Integer> cvmaxs = null;
	private Map<String, Long> cas = null;
	private Vector<String> cols = null;

	private Dataclient(String namespace, String table) {
		ns = namespace;
		tb = table;
	}

	public static Dataclient getinstance(String namespace, String table) {
		return new Dataclient(namespace, table);
	}

	public Dataclient key(String existkey) {
		key = existkey;
		return this;
	}

	public Dataclient columns(int numofcolumns) {
		cols = new Vector<String>(numofcolumns);
		return this;
	}

	public Dataclient columnvalues(int numofcolumnvalues) {
		cvs = new Hashtable<String, String>(numofcolumnvalues);
		cvmaxs = new Hashtable<String, Integer>(numofcolumnvalues);
		return this;
	}

	public Dataclient columnamounts(int numofcolumnamounts) {
		cas = new Hashtable<String, Long>(numofcolumnamounts);
		return this;
	}

	public Dataclient add4create(String column, String value, int max) {
		cvs.put(column, value);
		cvmaxs.put(column, max);
		return this;
	}

	public Dataclient add4increment(String column, long amount)  {
		cas.put(column, amount);
		return this;
	}

	public Dataclient add4modify(String column, String value) {
		if (value == null) {
			value = "";
		}
		cvs.put(column, value);
		return this;
	}

	public Dataclient add(String column) {
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
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_TABLE_KEY, tb);
			params.put(CS.PARAM_COLUMNVALUES_KEY, cvs);
			params.put(CS.PARAM_COLUMNMAXVALUES_KEY, cvmaxs);
			String[] iport = Bigclient.distributebigdata(ns, key);
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
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_DELETE);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_TABLE_KEY, tb);
			params.put(CS.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, key);
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
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_MODIFY);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_TABLE_KEY, tb);
			params.put(CS.PARAM_COLUMNVALUES_KEY, cvs);
			String[] iport = Bigclient.distributebigdata(ns, key);
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
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_TABLE_KEY, tb);
			params.put(CS.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, key);
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
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_INCREMENT);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_TABLE_KEY, tb);
			params.put(CS.PARAM_COLUMNAMOUNTS_KEY, cas);
			String[] iport = Bigclient.distributebigdata(ns, key);
			
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
