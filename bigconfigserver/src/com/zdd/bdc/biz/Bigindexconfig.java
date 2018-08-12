package com.zdd.bdc.biz;

import java.net.URLDecoder;
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
				Charset.forName("UTF-8"));

		Map<String, String> hash_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> iport_filehash = new Hashtable<String, String>();

		for (String line : lines) {
			if (line.indexOf(STATIC.KEY_SPLIT_VAL) > 0) {
				String key = URLDecoder.decode(line.substring(0, line.indexOf(STATIC.KEY_SPLIT_VAL)), "UTF-8");
				String value = URLDecoder.decode(line.substring(line.indexOf(STATIC.KEY_SPLIT_VAL) + 1), "UTF-8");
				String[] vals = value.split(STATIC.IP_SPLIT_PORT);
				String parentfolder = vals[0];
				String filehash = vals[1];
				String ip = vals[2];
				String port = vals[3];
				parentfolderip_port.put(parentfolder + STATIC.IP_SPLIT_PORT + ip, port);
				iport_filehash.put(ip + STATIC.IP_SPLIT_PORT + port, filehash);
				String ipport = ip + STATIC.IP_SPLIT_PORT + port;

				if (key.contains("-")) {
					int start = Integer.parseInt(key.split("-")[0]);
					int end = Integer.parseInt(key.split("-")[1]);
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
