package com.zdd.bdc.client.biz;

import java.util.HashMap;
import java.util.Map;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.client.util.STATIC;

public class Uniqueindexclient {

	private String index = null;
	private String ns = null;

	private Uniqueindexclient(String namespace, String theindex) {
		ns = namespace;
		index = theindex;
	}

	public static Uniqueindexclient getinstance(String namespace, String index) {
		return new Uniqueindexclient(namespace, index);
	}

	public void createunique(String filter, String key) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(5);
		params.put(STATIC.PARAM_KEY_KEY, key);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		params.put(STATIC.PARAM_FILTERS_KEY, filter);
		params.put(STATIC.PARAM_ADDITIONAL, STATIC.splitenc(Configclient
				.getinstance(ns, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(STATIC.urlencode(filter))).length);

		String[] iport = Bigclient.distributebiguniqueindex(ns, filter, index);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
	}

	public String readunique(String filter) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		params.put(STATIC.PARAM_FILTERS_KEY, filter);
		params.put(STATIC.PARAM_ADDITIONAL, STATIC.splitenc(Configclient
				.getinstance(ns, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(STATIC.urlencode(filter))).length);
		String[] iport = Bigclient.distributebiguniqueindex(ns, filter, index);
		return (String) Objectutil.convert(
				Theclient.request(iport[0], Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
	}

}
