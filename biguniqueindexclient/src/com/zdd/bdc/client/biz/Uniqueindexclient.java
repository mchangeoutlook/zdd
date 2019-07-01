package com.zdd.bdc.client.biz;

import java.util.HashMap;
import java.util.Map;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.ex.Theclientprocess;
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
		String scale = Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
				.read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX + filter);
		if (scale == null || scale.trim().isEmpty()
				|| scale.equals(Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter))) {
			createunique(Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter), filter, key);
		} else {
			if (readuniquebeforescale(filter)==null) {
				createunique(scale, filter, key);
			} else {
				throw new Exception(STATIC.DUPLICATE);
			}
		}
	}
	
	public void modifyunique(String filter, String key) throws Exception {
		String scale = Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
				.read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX + filter);
		if (scale == null || scale.trim().isEmpty()
				|| scale.equals(Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter))) {
			modifyunique(Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter), filter, key);
		} else {
			try {
				createunique(scale, filter, key);
			}catch(Exception e) {
				if (e.getMessage()!=null&&e.getMessage().contains(STATIC.DUPLICATE)) {
					modifyunique(scale, filter, key);
				}
			}
			
		}
	}
	
	private void modifyunique(String servergroups, String filter, String key) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(6);
		params.put(STATIC.PARAM_KEY_KEY, key);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_MODIFY);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		params.put(STATIC.PARAM_FILTERS_KEY, filter);
		params.put(STATIC.PARAM_ADDITIONAL, servergroups);
		String[] iport = Bigclient.distributebiguniqueindex(ns,
				servergroups, index);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
	}
	
	public void createunique(String servergroups, String filter, String key) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(6);
		params.put(STATIC.PARAM_KEY_KEY, key);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		params.put(STATIC.PARAM_FILTERS_KEY, filter);
		params.put(STATIC.PARAM_ADDITIONAL, servergroups);
		String[] iport = Bigclient.distributebiguniqueindex(ns,
				servergroups, index);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
	}

	private String readuniquebeforescale(String filter) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(5);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
		params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
		params.put(STATIC.PARAM_INDEX_KEY, index);
		params.put(STATIC.PARAM_FILTERS_KEY, filter);
		params.put(STATIC.PARAM_ADDITIONAL, Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter));
		String[] iport = Bigclient.distributebiguniqueindex(ns,
				Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter), index);
		return (String) Objectutil.convert(
				Theclient.request(iport[0], Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
	}
	
	public String readunique(String filter) throws Exception {
		String returnvalue = readuniquebeforescale(filter);
		if (returnvalue == null) {
			String scale = Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
					.read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX + filter);
			if (scale == null || scale.trim().isEmpty()
					|| scale.equals(Configclient.getinstance(ns, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter))) {
				return returnvalue;
			} else {
				Map<String, Object> params = new HashMap<String, Object>(5);
				params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
				params.put(STATIC.PARAM_NAMESPACE_KEY, ns);
				params.put(STATIC.PARAM_INDEX_KEY, index);
				params.put(STATIC.PARAM_FILTERS_KEY, filter);
				params.put(STATIC.PARAM_ADDITIONAL, scale);
				String[] iport = Bigclient.distributebiguniqueindex(ns, scale, index);
				return (String) Objectutil.convert(Theclient.request(iport[0], Integer.parseInt(iport[1]),
						Objectutil.convert(params), null, null));
			}
		} else {
			return returnvalue;
		}
	}

	public void readallroots(String ip, int port, String filter, Theclientprocess cp) throws Exception {
		Theclient.request(ip, port, Objectutil.convert(STATIC.splitenc(ns, filter)), null, cp);
	}

	public void readoneleaf(String ip, int port, String filter, String leaf, Theclientprocess cp) throws Exception {
		Theclient.request(ip, port, Objectutil.convert(STATIC.splitenc(ns, filter, leaf)), null, cp);
	}

	public void readonecollision(String ip, int port, String filter, String collision, Theclientprocess cp)
			throws Exception {
		Theclient.request(ip, port, Objectutil.convert(STATIC.splitenc(ns, filter, "collision", collision)), null, cp);
	}
	

}
