package com.zdd.bdc.server.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Bigunindexconfig {

	private static final Map<String, String> serverindexcache = new Hashtable<String, String>();

	private static String readiport(String namespace, String servergroups, int serverindex) throws Exception {

		String key = STATIC.splitenc(namespace, servergroups, String.valueOf(serverindex));
		if (serverindexcache.get(key) == null) {
			Vector<String> serverinfo = Fileconfigutil.readmany(servergroups, namespace,
					STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
			String[] serverinfoparts = STATIC.splitenc(serverinfo.get(serverindex));
			String iport = STATIC.splitiport(serverinfoparts[1], serverinfoparts[2]);
			serverindexcache.put(key, iport);
		}
		return serverindexcache.get(key);
	}

	private static final Map<String, String> servernumcache = new Hashtable<String, String>();

	private static String readservernum(String namespace, String servergroups) throws Exception {
		String servernumcachekey = STATIC.splitenc(namespace, servergroups);
		if (servernumcache.get(servernumcachekey) == null) {
			String servernum = String.valueOf(
					Fileconfigutil.readmany(servergroups, namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).size());
			servernumcache.put(servernumcachekey, servernum);
		}
		return servernumcache.get(servernumcachekey);
	}

	private static final Map<String, String> portcache = new Hashtable<String, String>();

	private static synchronized void initportcache(String namespace, String key) throws Exception {
		if (portcache.get(key) == null) {
			Vector<String> multivaluekeys = Fileconfigutil.readallmultivaluekeys(namespace,
					STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
			for (String multivaluekey : multivaluekeys) {
				Vector<String> multivalues = Fileconfigutil.readmany(multivaluekey, namespace,
						STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
				for (String val : multivalues) {
					String[] serverinfoparts = STATIC.splitenc(val);
					String folder = serverinfoparts[0];
					String ip = serverinfoparts[1];
					String port = serverinfoparts[2];

					String folderipkey = STATIC.splitenc(folder, ip);

					String portcachekey = STATIC.splitenc(namespace, folderipkey);
					if (portcache.get(portcachekey) != null) {
						break;
					} else {
						portcache.put(portcachekey, port);
					}
				}
			}
		}
	}

	public static String read(String namespace, String key) throws Exception {
		if (key.endsWith(STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX)) {
			return readservernum(namespace,
					key.substring(0, key.indexOf(STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX)));
		} else if (key.contains(STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE)) {
			String[] parts = key.split(STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE);
			return readiport(namespace, parts[0], Integer.parseInt(parts[1]));
		} else {
			String cachekey = STATIC.splitenc(namespace, key);
			if (portcache.get(cachekey) == null) {
				initportcache(namespace, cachekey);
			}
			return portcache.get(cachekey);
		}
	}
}
