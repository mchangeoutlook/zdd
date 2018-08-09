package com.zdd.bdc.biz;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Bigdataconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		List<String> lines = Files.readAllLines(Configserver.configfolder.resolve(namespace).resolve("bigdata"),
				Charset.forName("UTF-8"));

		Map<String, String> date_ipport = new Hashtable<String, String>();

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
				date_ipport.put(key, ipport);
			}
		}

		Object[] sortedconfigkeys = date_ipport.keySet().toArray();
		Arrays.sort(sortedconfigkeys);
		boolean tilltoday = false;
		Date today = new Date();
		for (int i = 0; i < sortedconfigkeys.length; i++) {
			String key = sortedconfigkeys[i].toString();
			date_ipport.put(key, date_ipport.get(key));

			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(key));
			c.add(Calendar.DATE, 1);
			while (i + 1 < sortedconfigkeys.length && !new SimpleDateFormat("yyyy-MM-dd").format(c.getTime())
					.equals(sortedconfigkeys[i + 1].toString())) {
				date_ipport.put(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()), date_ipport.get(key));
				c.add(Calendar.DATE, 1);
			}
			if (i + 1 >= sortedconfigkeys.length) {
				while (c.getTime().compareTo(today) <= 0) {
					tilltoday = true;
					date_ipport.put(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()), date_ipport.get(key));
					c.add(Calendar.DATE, 1);
				}
			}
		}
		Calendar c = Calendar.getInstance();
		if (tilltoday) {
			c.setTime(today);
		} else {
			c.setTime(
					new SimpleDateFormat("yyyy-MM-dd").parse(sortedconfigkeys[sortedconfigkeys.length - 1].toString()));
		}
		c.add(Calendar.DATE, 1);
		int moredays = 365 * 10;// 10years;
		while (moredays > 0) {
			String key = null;
			if (tilltoday) {
				key = new SimpleDateFormat("yyyy-MM-dd").format(today);
			} else {
				key = sortedconfigkeys[sortedconfigkeys.length - 1].toString();
			}
			date_ipport.put(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()), date_ipport.get(key));
			c.add(Calendar.DATE, 1);
			moredays--;
		}

		Map<String, String> all = new Hashtable<String, String>();
		all.putAll(date_ipport);
		all.putAll(parentfolderip_port);
		all.putAll(parentfolderip_filehash);
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigdataconfig [" + parentfolderip_port.size()
				+ "] text servers and [" + date_ipport.size() + "] days under namespace [" + namespace + "]");
		System.out.println(new Date()
				+ " ==== please restart configserver or add new data distribution and restart configserver before "
				+ c.getTime());
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}
}
