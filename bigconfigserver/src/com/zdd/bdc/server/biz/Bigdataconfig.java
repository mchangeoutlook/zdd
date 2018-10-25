package com.zdd.bdc.biz;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.CS;
import com.zdd.bdc.util.Filekvutil;
import com.zdd.bdc.util.SS;

public class Bigdataconfig {

	private static final Map<String, Map<String, String>> config = new Hashtable<String, Map<String, String>>();

	public static void init(String namespace) throws Exception {
		String active = Filekvutil.config("active", namespace, CS.REMOTE_CONFIG_BIGDATA);
		String[] dates = CS.splitenc(active);

		Map<String, String> date_ipport = new Hashtable<String, String>();

		Map<String, String> parentfolderip_port = new Hashtable<String, String>();
		Map<String, String> iport_filehash = new Hashtable<String, String>();
		for (String key : dates) {
			String[] values = Filekvutil.configs(key, namespace, CS.REMOTE_CONFIG_BIGDATA);
			Vector<String> ipports = new Vector<String>(values.length);
			for (String val : values) {
				String[] vals = CS.splitenc(val);
				String parentfolder = vals[0];
				String filehash = vals[1];
				String ip = vals[2];
				String port = vals[3];
				parentfolderip_port.put(CS.splitenc(parentfolder, ip), port);
				iport_filehash.put(CS.splitiport(ip, port), filehash);
				ipports.add(CS.splitiport(ip, port));
			}
			String[] ipportsarray = new String[ipports.size()];
			date_ipport.put(key, CS.splitenc(ipports.toArray(ipportsarray)));

		}

		Object[] sortedconfigkeys = date_ipport.keySet().toArray();
		Arrays.sort(sortedconfigkeys);
		boolean tilltoday = false;
		Date today = new Date();
		for (int i = 0; i < sortedconfigkeys.length; i++) {
			String key = sortedconfigkeys[i].toString();
			date_ipport.put(key, date_ipport.get(key));

			Calendar c = Calendar.getInstance();
			c.setTime(SS.FORMAT_yMd.parse(key));
			c.add(Calendar.DATE, 1);
			while (i + 1 < sortedconfigkeys.length
					&& !SS.FORMAT_yMd.format(c.getTime()).equals(sortedconfigkeys[i + 1].toString())) {
				date_ipport.put(SS.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
				c.add(Calendar.DATE, 1);
			}
			if (i + 1 >= sortedconfigkeys.length) {
				while (c.getTime().compareTo(today) <= 0) {
					tilltoday = true;
					date_ipport.put(SS.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
					c.add(Calendar.DATE, 1);
				}
			}
		}
		Calendar c = Calendar.getInstance();
		if (tilltoday) {
			c.setTime(today);
		} else {
			c.setTime(SS.FORMAT_yMd.parse(sortedconfigkeys[sortedconfigkeys.length - 1].toString()));
		}
		c.add(Calendar.DATE, 1);
		int moredays = 365 * 10;// 10years;
		while (moredays > 0) {
			String key = null;
			if (tilltoday) {
				key = SS.FORMAT_yMd.format(today);
			} else {
				key = sortedconfigkeys[sortedconfigkeys.length - 1].toString();
			}
			date_ipport.put(SS.FORMAT_yMd.format(c.getTime()), date_ipport.get(key));
			c.add(Calendar.DATE, 1);
			moredays--;
		}

		Map<String, String> all = new Hashtable<String, String>();
		all.putAll(date_ipport);
		all.putAll(parentfolderip_port);
		all.putAll(iport_filehash);
		config.put(namespace, all);
		System.out.println(new Date() + " ==== generated bigdataconfig [" + all + "] [" + parentfolderip_port.size()
				+ "] text servers and [" + date_ipport.size() + "] days under namespace [" + namespace + "]");
		System.out.println(new Date()
				+ " ==== please restart configserver or add new data distribution and restart configserver before "
				+ c.getTime());
	}

	public static String read(String namespace, String key) {
		return config.get(namespace).get(key);
	}
}
