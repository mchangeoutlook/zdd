package com.xinzyan.yanxin.stat.main;

import java.util.Date;
import java.util.Vector;

import com.tenotenm.yanxin.util.Reuse;
import com.xinzyan.yanxin.stat.util.Stat;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Pagedindexclient;

/**
 * @author mido how to run: nohup /root/jdk9/bin/java -cp ../commonclientlibs/yanxinstat.jar:../commonclientlibs/yanxin.jar:../commonclientlibs/biguniqueindexclient.jar:../commonclientlibs/bigexclient.jar:../commonclientlibs/bigconfigclient.jar:../commonclientlibs/bigcomclientutil.jar:../commonclientlibs/bigpagedindexclient.jar com.xinzyan.yanxin.stat.main.Start > log.runyanxinstat &
 */
public class Start {

	public static void main(String[] s) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (Configclient.running()) {
					try {
						String yesterday = Reuse.yyyyMMdd(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
						Vector<String> statkeys = Pagedindexclient
								.getinstance(Reuse.namespace_yanxin, "stat-" + yesterday).addfilter("stat").read(0);
						if (statkeys != null && !statkeys.isEmpty()) {
							try {
								Thread.sleep(60 * 60000);
							} catch (InterruptedException e) {
								// do nothing
							}
						} else {
							String statkey = Bigclient.newbigdatakey();
								Pagedindexclient.getinstance(Reuse.namespace_yanxin, "stat-" + yesterday)
										.addfilter("stat").create(statkey, 0);
							Stat.gothrough(yesterday, statkey);
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
