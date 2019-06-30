package com.xinzyan.yanxin.stat.main;

import java.util.Date;
import java.util.Map;

import com.tenotenm.yanxin.util.Reuse;
import com.xinzyan.yanxin.stat.util.Stat;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Dataclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;

public class Start {

	public static void main(String[] s) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (Configclient.running()) {
					try {
						String yesterday = Reuse.yyyyMMdd(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
						String statkey = Uniqueindexclient.getinstance(Reuse.namespace_yanxin, "stat-" + yesterday).readunique(Reuse.filter_paged);
						boolean needgothrough = false;
						if (statkey != null && !statkey.trim().isEmpty()) {
							Map<String, String> s = Dataclient.getinstance(Reuse.namespace_yanxin, "stat").key(statkey).add("total").read(Reuse.app_data);
							if (s==null||s.isEmpty()||s.get("total")==null||s.get("total").equals("0")) {
								needgothrough = true;
							}
						} else {
							needgothrough = true;
						}
						if (needgothrough) {
							if (statkey==null) {
								statkey = Bigclient.newbigdatakey();
								Uniqueindexclient.getinstance(Reuse.namespace_yanxin, "stat-" + yesterday).createunique(Reuse.filter_paged, statkey);
							}	
							Stat.gothrough(yesterday, statkey);
						}
						try {
							Thread.sleep(120 * 60000);
						} catch (InterruptedException e) {
							// do nothing
						}
					} catch (Exception e) {
						System.out.println(new Date()
								+ " ==== failed to start stating due to below exception, will restart tomorrow: ");
						e.printStackTrace();
					}
				}
			}

		}).start();

	}

}
