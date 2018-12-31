package com.zdd.bdc.client.biz;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.client.util.STATIC;

public class Pagedindexclient {

	private String index = null;
	private String ns = null;
	private Vector<String> filters = new Vector<String>(20);

	private Pagedindexclient(String namespace, String theindex) {
		ns = namespace;
		index = theindex;
	}

	public static Pagedindexclient getinstance(String namespace, String index) {
		return new Pagedindexclient(namespace, index);
	}

	public Pagedindexclient addfilter(String filter) {
		filters.add(filter);
		return this;
	}

	public void create(String key, long pagenum) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(6);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_INDEX_KEY, index);
			filters.add(0, String.valueOf(pagenum));
			params.put(STATIC.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigpagedindex(ns, filters, index);
			Theclient.request(iport[0],
					Integer.parseInt(iport[1]),
					Objectutil.convert(params), null, null);
		} finally {
			clear();
		}
	}

	private Long indexincrement(String version) throws Exception {
		try {
		Map<String, Object> params = new HashMap<String, Object>(6);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_INCREMENT);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		filters.add(version);
		params.put(STATIC.PARAM_FILTERS_KEY, filters);
		String[] iport = Bigclient.distributebigpagedindex(ns, filters, index);
		return (Long) Objectutil.convert(Theclient.request(iport[0],
				Integer.parseInt(iport[1]),
				Objectutil.convert(params), null, null));
		}finally {
			filters.remove(filters.size()-1);
		}
	}
	
	public Long create(String key, String version) throws Exception {
		try {
			Long itemsonepage = Long.parseLong(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE).read(STATIC.REMOTE_CONFIGKEY_ITEMSONEPAGE));
			Long indexincremented = indexincrement(version);
			Map<String, Object> params = new HashMap<String, Object>(6);
			params.put(STATIC.PARAM_KEY_KEY, key);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_INDEX_KEY, index);
			filters.add(0, String.valueOf((indexincremented.longValue()-1)/itemsonepage.longValue()));
			params.put(STATIC.PARAM_FILTERS_KEY, filters);
			params.put(STATIC.PARAM_VERSION_KEY, version);
			String[] iport = Bigclient.distributebigpagedindex(ns, filters, index);
			 Theclient.request(iport[0],
					Integer.parseInt(iport[1]),
					Objectutil.convert(params), null, null);
			return indexincremented;
		} finally {
			clear();
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> read(long pagenum) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
			params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
			params.put(STATIC.PARAM_INDEX_KEY, index);
			filters.add(0, String.valueOf(pagenum));
			params.put(STATIC.PARAM_FILTERS_KEY, filters);
			String[] iport = Bigclient.distributebigpagedindex(ns, filters, index);
			
			return (Vector<String>) Objectutil
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
