package com.zdd.bdc.biz;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Bigindexconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		List<String> lines = Files.readAllLines(Configserver.configfolder.resolve(namespace).resolve("bigindex"),
				Charset.forName("UTF-8"));

		Map<String, String> hash_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> parentfolderip_filehash = new Hashtable<String, String>();

		for (String line : lines) {
			if (line.indexOf("#") > 0) {
				String key = URLDecoder.decode(line.substring(0, line.indexOf("#")), "UTF-8");
				String value = URLDecoder.decode(line.substring(line.indexOf("#") + 1), "UTF-8");
				String[] values = value.split("#");
				String ipport = "";
				for (String val : values) {
					String[] vals = val.split(":");
					String parentfolder = vals[0];
					String filehash = vals[1];
					String ip = vals[2];
					String port = vals[3];
					parentfolderip_port.put(parentfolder + ":" + ip, port);
					parentfolderip_filehash.put(parentfolder + ":filehash:" + ip, filehash);
					if (!ipport.equals("")) {
						ipport += "#";
					}
					ipport += ip + ":" + port;
				}

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
		all.putAll(parentfolderip_filehash);
		all.put("maxindexservers", String.valueOf(hash_ipport.size()));
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigindexconfig [" + parentfolderip_port.size()
				+ "] index servers and [" + hash_ipport.size() + "] hashes under namespace [" + namespace + "]");
		System.out.println(
				new Date() + " ==== please restart configserver to make new index distribution to take effect");
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}

}
