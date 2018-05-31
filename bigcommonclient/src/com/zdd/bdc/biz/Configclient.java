package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Configclient {
	private String ns = null;
	private String file = null;
	private Map<String, Map<String, Map<String, String>>> configkeysvalues = null;

	private Configclient(String namespace, String configfile) {
		ns = namespace;
		file = configfile;
	}

	public static Configclient getinstance(String namespace, String configfile) {
		return new Configclient(namespace, configfile);
	}

	public String read(String configkey) throws Exception {
		configkeys(1);
		configkeysvalues.get(ns).get(file).put(configkey, "");
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("data", configkeysvalues);
			params.put("action", "read");
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Map<String, String>>> res = (Map<String, Map<String, Map<String, String>>>) Objectutil
					.convert(Theclient.request("localhost", 9997, Objectutil.convert(params), null));
			if (res.get(ns) == null) {
				return null;
			}
			if (res.get(ns).get(file) == null) {
				return null;
			}
			return res.get(ns).get(file).get(configkey);
		} finally {
			clear();
		}
	}

	public Configclient configkeys(int numofkeys) {
		configkeysvalues = new Hashtable<String, Map<String, Map<String, String>>>(1);
		configkeysvalues.put(ns, new Hashtable<String, Map<String, String>>(1));
		configkeysvalues.get(ns).put(file, new Hashtable<String, String>(numofkeys));
		return this;
	}

	public Configclient add4create(String configkey, String configvalue) {
		configkeysvalues.get(ns).get(file).put(configkey, configvalue);
		return this;
	}

	public void create() throws Exception {
		if (configkeysvalues.isEmpty()) {
			throw new Exception(".configkeys.add4create.create");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("data", configkeysvalues);
			params.put("action", "change");
			Objectutil.convert(Theclient.request("localhost", 9997, Objectutil.convert(params), null));
		} finally {
			clear();
		}
	}

	private void clear() {
		ns = null;
		if (configkeysvalues != null) {
			configkeysvalues.clear();
		}
		configkeysvalues = null;
	}

}
