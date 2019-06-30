package com.xinzyan.yanxin.stat.util;

import java.util.Date;

import com.tenotenm.yanxin.entities.Yanxin;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.xinzyan.yanxin.stat.util.Readroot;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Dataclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.util.STATIC;

public class Stat {
	
	public static void statyxaccount(String yesterday, String statkey, String yxaccountkey) throws Exception {
		Yxaccount yxaccount = new Yxaccount();
		try {
			yxaccount.read(yxaccountkey);
		}catch(Exception e) {
			if (Reuse.NOTFOUND.equals(e.getMessage())) {
				yxaccount = null;
			} else {
				throw e;
			}
		}
		if (yxaccount!=null) {
			String timecreate = Reuse.yyyyMMdd(yxaccount.getTimecreate());
			Yanxin yx = Bizutil.readyanxin(yxaccount, Reuse.yyyyMMdd(yesterday));
			Dataclient d = Dataclient.getinstance(Reuse.namespace_yanxin, "stat").key(statkey);
			d.add4increment("total", 1);
			if (timecreate.equals(yesterday)) {
				d.add4increment("new", 1);
			}
			if (yx!=null) {
				d.add4increment("write", 1);
			}
			d.increment(Reuse.app_data);
		}
	}
	
	public static void gothrough(String yesterday, String statkey) throws Exception {

		int uniqueservers = Integer
				.parseInt(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
						.read(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
								.read("Yxaccount-ukey")+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX));
		for (int j = 0; j < uniqueservers; j++) {
			final int server = j;
			new Thread(new Runnable() {

				@Override
				public void run() {
					String[] ipport = STATIC.splitiport(
							Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
									.read(Configclient
											.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
											.read("Yxaccount-ukey") +STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE+  server));
					System.out.println(new Date() + " ==== starting stat [" + Reuse.namespace_yanxin
							+ "][Yxaccount-ukey] on [" + ipport[0] + "][" + ipport[1] + "]");
					try {
						Uniqueindexclient.getinstance(Reuse.namespace_yanxin, null).readallroots(ipport[0],
								Integer.parseInt(ipport[1]), "Yxaccount-ukey",
								new Readroot(Reuse.namespace_yanxin, "Yxaccount-ukey", yesterday, statkey, ipport[0],
										Integer.parseInt(ipport[1])));
						System.out.println(new Date() + " ==== success! done stat [" + Reuse.namespace_yanxin + "]["
								+ "Yxaccount-ukey" + "] on [" + ipport[0] + "][" + ipport[1] + "]");
					} catch (Exception e) {
						System.out.println(new Date() + " ==== fail! terminated stat [" + Reuse.namespace_yanxin + "]["
								+ "Yxaccount-ukey" + "] on [" + ipport[0] + "][" + ipport[1] + "] due to below exception:");
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
