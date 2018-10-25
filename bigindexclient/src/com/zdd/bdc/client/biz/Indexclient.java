package com.zdd.bdc.client.biz;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.client.util.CS;

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
			Map<String, Object> params = new HashMap<String, Object>(6);
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_INDEX_KEY, index);
			params.put(CS.PARAM_PAGENUM_KEY, pagenum);
			params.put(CS.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigindex(ns, pagenum, index);
			Theclient.request(iport[0],
					Integer.parseInt(iport[1]),
					Objectutil.convert(params), null, null);
		} finally {
			clear();
		}
	}

	public void create(String key, long pagenum, String version) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(6);
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_INDEX_KEY, index);
			params.put(CS.PARAM_PAGENUM_KEY, pagenum);
			params.put(CS.PARAM_FILTERS_KEY, filters);
			if (version!=null&&!version.trim().isEmpty()) {
				params.put(CS.PARAM_VERSION_KEY, version);
			}
			String[] iport = Bigclient.distributebigindex(ns, pagenum, index);
			Theclient.request(iport[0],
					Integer.parseInt(iport[1]),
					Objectutil.convert(params), null, null);
		} finally {
			clear();
		}
	}

	public void createunique(String key) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put(CS.PARAM_KEY_KEY, key);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_INDEX_KEY, index);
			params.put(CS.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigindex(ns, null, index);
			Theclient.request(iport[0],
					Integer.parseInt(iport[1]),
					Objectutil.convert(params), null, null);
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> read(long pagenum, int numofdata) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_INDEX_KEY, index);
			params.put(CS.PARAM_PAGENUM_KEY, pagenum);
			params.put(CS.PARAM_NUMOFDATA, numofdata);
			
			params.put(CS.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigindex(ns, pagenum, index);
			
			return (Vector<String>) Objectutil
					.convert(Theclient.request(iport[0],
							Integer.parseInt(iport[1]),
							Objectutil.convert(params), null, null));
		} finally {
			clear();
		}
	}

	public String readunique() throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(4);
			params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
			params.put(CS.PARAM_NAMESPACE_KEY, ns);
			params.put(CS.PARAM_INDEX_KEY, index);
			params.put(CS.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigindex(ns, null, index);
			return (String) Objectutil
					.convert(Theclient.request(iport[0],
							Integer.parseInt(iport[1]),
							Objectutil.convert(params), null, null));
		} finally {
			clear();
		}
	}

	private void clear() {
		index = null;
		ns = null;
		if (filters != null) {
			filters.clear();
		}
		filters = null;
	}
}
