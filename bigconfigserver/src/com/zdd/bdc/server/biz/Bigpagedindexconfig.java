package com.zdd.bdc.server.biz;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Bigpagedindexconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		String active = Fileconfigutil.readone("active", namespace, STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		String[] hashes = STATIC.splitenc(active);

		Map<String, String> hash_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> iport_filehash = new Hashtable<String, String>();

		for (String key : hashes) {
				String[] vals = STATIC.splitenc(Fileconfigutil.readone(key, namespace, STATIC.REMOTE_CONFIG_BIGPAGEDINDEX));
				String parentfolder = vals[0];
				String filehash = vals[1];
				String ip = vals[2];
				String port = vals[3];
				parentfolderip_port.put(STATIC.splitenc(parentfolder, ip), port);
				iport_filehash.put(STATIC.splitiport(ip, port), filehash);
				String ipport = STATIC.splitiport(ip, port);

				String[] fromto = STATIC.splitfromto(key);
				if (fromto.length==2) {
					int start = Integer.parseInt(fromto[0]);
					int end = Integer.parseInt(fromto[1]);
					for (int i = start; i <= end; i++) {
						hash_ipport.put(String.valueOf(i), ipport);
					}
				} else {
					hash_ipport.put(key, ipport);
				}
			
		}
		Map<String, String> all = new Hashtable<String, String>();
		all.putAll(hash_ipport);
		all.putAll(parentfolderip_port);
		all.putAll(iport_filehash);
		all.put(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS, String.valueOf(hash_ipport.size()));
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigpagedindexconfig ["+all+"] [" + parentfolderip_port.size()
				+ "] paged index servers and [" + hash_ipport.size() + "] hashes under namespace [" + namespace + "]");
		System.out.println(
				new Date() + " ==== please restart configserver to make new paged index distribution to take effect");
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}
	
}
