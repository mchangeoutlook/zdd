package com.zdd.bdc.client.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;

public class Dataclient {

	private String key = null;
	private String ns = null;
	private String tb = null;
	private Map<String, String> cvs = new Hashtable<String, String>(100);
	private Map<String, Integer> cvmaxs = new Hashtable<String, Integer>(100);
	private Map<String, Long> cas = new Hashtable<String, Long>(50);
	private Vector<String> cols = new Vector<String>(100);

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

	public Dataclient add4create(String column, String value, int morecapacity) {
		cvs.put(column, value);
		cvmaxs.put(column, morecapacity);
		return this;
	}

	public Dataclient add4increment(String column, long amount)  {
		cas.put(column, amount);
		return this;
	}

	public Dataclient add4modify(String column, String value) {
		cvs.put(column, value);
		return this;
	}

	public Dataclient add(String column) {
		cols.add(column);
		return this;
	}

	public String create(String app) throws Exception {
		if (cvs.isEmpty() || cvmaxs.isEmpty()) {
			throw new Exception("(.key).add4create.create");
		}
		try {
			if (key==null) {
				key = Bigclient.newbigdatakey();
			}
			Map<String, Object> params = new Hashtable<String, Object>(6);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNVALUES_KEY, cvs);
			params.put(STATIC.PARAM_COLUMNMAXVALUES_KEY, cvmaxs);
			String[] iport = Bigclient.distributebigdata(ns, app, key);
			Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
			return key;
		} finally {
			clear();
		}
	}

	public void delete(String app) throws Exception {
		if (key == null || cols.isEmpty()) {
			throw new Exception(".key.add.delete");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_DELETE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, app, key);
			Objectutil.convert(Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
		} finally {
			clear();
		}
	}
	

	public void modify(String app) throws Exception {
		if (key == null || cvs.isEmpty()) {
			throw new Exception(".key.add4modify.modify");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_MODIFY);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNVALUES_KEY, cvs);
			String[] iport = Bigclient.distributebigdata(ns, app, key);
			Theclient.request(iport[0], 
					Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> read(String app) throws Exception {
		if (key == null || cols.isEmpty()) {
			throw new Exception(".key.add.read");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNS_KEY, cols);
			String[] iport = Bigclient.distributebigdata(ns, app, key);
			return (Map<String, String>) Objectutil
					.convert(Theclient.request(iport[0], 
							Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> increment(String app) throws Exception {
		if (key == null || cas.isEmpty()) {
			throw new Exception(".key.add4increment.increment");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_INCREMENT);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_TABLE_KEY, tb);
			params.put(STATIC.PARAM_COLUMNAMOUNTS_KEY, cas);
			String[] iport = Bigclient.distributebigdata(ns, app, key);
			
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
