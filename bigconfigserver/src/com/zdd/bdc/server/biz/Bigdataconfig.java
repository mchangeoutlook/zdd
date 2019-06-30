package com.zdd.bdc.server.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Bigdataconfig {
	
	private static final Map<String, String> serverindexcache = new Hashtable<String, String>();
	private static String readiport(String namespace, String app, String date, int serverindex) throws Exception {
		
		String key = STATIC.splitenc(namespace, app, date, String.valueOf(serverindex));
		if (serverindexcache.get(key)==null) {
			Vector<String> serverinfo = Fileconfigutil.readmany(STATIC.splitenc(app, date), namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA);
			String[] serverinfoparts = STATIC.splitenc(serverinfo.get(serverindex));
			String iport = STATIC.splitiport(serverinfoparts[2], serverinfoparts[3]);
			serverindexcache.put(key, iport);
		}
		return serverindexcache.get(key);
	}
	
	private static final Map<String, String> servernumcache = new Hashtable<String, String>();
	private static String readdateservernum(String namespace, String app) throws Exception {
		String[] appserverdates = STATIC.splitenc(Fileconfigutil.readone(app+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX, namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA));
		Vector<String> dateservernum = new Vector<String>(appserverdates.length);
		for (String date:appserverdates) {
			String servernumcachekey = STATIC.splitenc(namespace, app, date);
			if (servernumcache.get(servernumcachekey)==null) {
				String servernum = String.valueOf(Fileconfigutil.readmany(STATIC.splitenc(app, date), namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA).size());
				String date_servernum = STATIC.splitenc(date, servernum);
				servernumcache.put(servernumcachekey, date_servernum);
			}
			String servernum = servernumcache.get(servernumcachekey);
			dateservernum.add(servernum);
		}
		return STATIC.splitenc(dateservernum);
	}
	
	private static final Map<String, String> portandbigfilehashcache = new Hashtable<String, String>();
	private static synchronized void initportandbigfilehashcache(String namespace, String key) throws Exception {
		if (portandbigfilehashcache.get(key)==null) {
			Vector<String> multivaluekeys = Fileconfigutil.readallmultivaluekeys(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA);
			for (String multivaluekey: multivaluekeys) {
				Vector<String> multivalues = Fileconfigutil.readmany(multivaluekey, namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA);
				for (String val:multivalues) {
					String[] serverinfoparts = STATIC.splitenc(val);
					String folder = serverinfoparts[0];
					String bigfilehash = serverinfoparts[1];
					String ip = serverinfoparts[2];
					String port = serverinfoparts[3];
					
					String folderipkey = STATIC.splitenc(folder, ip);
					String ipportkey = STATIC.splitiport(ip, port);
					
					String portcachekey = STATIC.splitenc(namespace, folderipkey);
					String bigfilehashkey = STATIC.splitenc(namespace, ipportkey);
					if (portandbigfilehashcache.get(portcachekey)!=null) {
						break;
					} else {
						portandbigfilehashcache.put(portcachekey, port);
						portandbigfilehashcache.put(bigfilehashkey, bigfilehash);
					}
				}
			}
		}
	}
	
	public static String read(String namespace, String key) throws Exception{
		if (key.endsWith(STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX)) {
			return readdateservernum(namespace, key.substring(0, key.indexOf(STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX)));
		} else if (key.contains(STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE)){
			String[] parts = key.split(STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE);
			String[] app_date = STATIC.splitenc(parts[0]);
			return readiport(namespace, app_date[0], app_date[1], Integer.parseInt(parts[1]));
		} else if (key.endsWith(STATIC.REMOTE_CONFIGKEY_SERVERPORTSUFFIX)){
			key=key.substring(0, key.lastIndexOf(STATIC.REMOTE_CONFIGKEY_SERVERPORTSUFFIX));
			String cachekey = STATIC.splitenc(namespace, key);
			if (portandbigfilehashcache.get(cachekey)==null) {
				initportandbigfilehashcache(namespace, cachekey);
			}
			return portandbigfilehashcache.get(cachekey);
		} else {
			return Fileconfigutil.readone(key, namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA);
		}
	}
}
