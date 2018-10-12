package com.zdd.bdc.biz;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.STATIC;

public class Bigdataconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		List<String> lines = Files.readAllLines(STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(STATIC.REMOTE_CONFIGFILE_BIGDATA),
				Charset.forName(STATIC.CHARSET_DEFAULT));

		Map<String, String> date_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> iport_filehash = new Hashtable<String, String>();
		for (String line : lines) {
			if (STATIC.commentget(line) == null) {
				String[] keyval = STATIC.keyval(line);
				String key = keyval[0];
				String value = keyval[1];
				String[] values = STATIC.split(value);
				Vector<String> ipports = new Vector<String>(values.length);
				for (String val : values) {
					String[] vals = STATIC.splitenc(val);
					String parentfolder = vals[0];
					String filehash = vals[1];
					String ip = vals[2];
					String port = vals[3];
					parentfolderip_port.put(STATIC.splitenc(parentfolder, ip), port);
					iport_filehash.put(STATIC.splitenc(ip, port), filehash);
					ipports.add(STATIC.splitenc(ip, port));
				}
				String[] ipportsarray = new String[ipports.size()];
				date_ipport.put(key, STATIC.split(ipports.toArray(ipportsarray)));
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
			c.setTime(STATIC.FORMAT_yMd.parse(key));
			c.add(Calendar.DATE, 1);
			while (i + 1 < sortedconfigkeys.length && !STATIC.FORMAT_yMd.format(c.getTime())
					.equals(sortedconfigkeys[i + 1].toString())) {
				date_ipport.put(STATIC.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
				c.add(Calendar.DATE, 1);
			}
			if (i + 1 >= sortedconfigkeys.length) {
				while (c.getTime().compareTo(today) <= 0) {
					tilltoday = true;
					date_ipport.put(STATIC.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
					c.add(Calendar.DATE, 1);
				}
			}
		}
		Calendar c = Calendar.getInstance();
		if (tilltoday) {
			c.setTime(today);
		} else {
			c.setTime(
					STATIC.FORMAT_yMd.parse(sortedconfigkeys[sortedconfigkeys.length - 1].toString()));
		}
		c.add(Calendar.DATE, 1);
		int moredays = 365 * 10;// 10years;
		while (moredays > 0) {
			String key = null;
			if (tilltoday) {
				key = STATIC.FORMAT_yMd.format(today);
			} else {
				key = sortedconfigkeys[sortedconfigkeys.length - 1].toString();
			}
			date_ipport.put(STATIC.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
			c.add(Calendar.DATE, 1);
			moredays--;
		}

		Map<String, String> all = new Hashtable<String, String>();
		all.putAll(date_ipport);
		all.putAll(parentfolderip_port);
		all.putAll(iport_filehash);
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigdataconfig ["+all+"] [" + parentfolderip_port.size()
				+ "] text servers and [" + date_ipport.size() + "] days under namespace [" + namespace + "]");
		System.out.println(new Date()
				+ " ==== please restart configserver or add new data distribution and restart configserver before "
				+ c.getTime());
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}
}
