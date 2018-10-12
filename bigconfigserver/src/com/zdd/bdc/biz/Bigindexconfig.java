package com.zdd.bdc.biz;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.zdd.bdc.util.STATIC;

public class Bigindexconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		List<String> lines = Files.readAllLines(STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(STATIC.REMOTE_CONFIGFILE_BIGINDEX),
				Charset.forName(STATIC.CHARSET_DEFAULT));

		Map<String, String> hash_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> iport_filehash = new Hashtable<String, String>();

		for (String line : lines) {
			if (STATIC.commentget(line) == null) {
				String[] keyval = STATIC.keyval(line);
				String key = keyval[0];
				String value = keyval[1];
				String[] vals = STATIC.splitenc(value);
				String parentfolder = vals[0];
				String filehash = vals[1];
				String ip = vals[2];
				String port = vals[3];
				parentfolderip_port.put(STATIC.splitenc(parentfolder, ip), port);
				iport_filehash.put(STATIC.splitenc(ip, port), filehash);
				String ipport = STATIC.splitenc(ip, port);

				String[] fromto = STATIC.fromto(key);
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
		}
		Map<String, String> all = new Hashtable<String, String>();
		all.putAll(hash_ipport);
		all.putAll(parentfolderip_port);
		all.putAll(iport_filehash);
		all.put(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS, String.valueOf(hash_ipport.size()));
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigindexconfig ["+all+"] [" + parentfolderip_port.size()
				+ "] index servers and [" + hash_ipport.size() + "] hashes under namespace [" + namespace + "]");
		System.out.println(
				new Date() + " ==== please restart configserver to make new index distribution to take effect");
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}

}
