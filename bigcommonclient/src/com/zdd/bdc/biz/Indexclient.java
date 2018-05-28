package com.zdd.bdc.biz;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Indexclient {
	
	private String index = null;
	private String ns = null;
	private Vector<String> filters = null;
	
	private Indexclient(String namespace, String theindex) {
		ns = namespace;
		index = theindex;
	}
	
	public static Indexclient getinstance(String namespace, String index) {
		return new Indexclient(namespace, index);
	}
	
	public Indexclient filters(int numofilters) {
		filters = new Vector<String>(numofilters);
		return this;
	}

	public Indexclient add(String filter) {
		filters.add(filter);
		return this;
	}
	public void create(String key, long pagenum) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put("key", key);
			params.put("action", "create");
			params.put("ns", ns);
			params.put("index", index);
			params.put("pagenum", pagenum);
			params.put("filters", filters);
			Theclient.request("localhost", 9998, Objectutil.convert(params), null);
		}finally {
			clear();
		}
	}

	public void createunique(String key) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put("key", key);
			params.put("action", "create");
			params.put("ns", ns);
			params.put("index", index);
			params.put("filters", filters);
			Theclient.request("localhost", 9998, Objectutil.convert(params), null);
		}finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> read(long pagenum) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put("index", index);
			params.put("action", "read");
			params.put("ns", ns);
			params.put("pagenum", pagenum);
			params.put("filters", filters);
			return (Vector<String>)Objectutil.convert(Theclient.request("localhost", 9998, Objectutil.convert(params), null));
		}finally {
			clear();
		}
	}

	public String readunique() throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put("index", index);
			params.put("action", "read");
			params.put("ns", ns);
			params.put("filters", filters);
			return (String)Objectutil.convert(Theclient.request("localhost", 9998, Objectutil.convert(params), null));
		}finally {
			clear();
		}
	}

	private void clear() {
		index = null;
		ns = null;
		if (filters!=null) {
			filters.clear();
		}
		filters = null;
	}
}
