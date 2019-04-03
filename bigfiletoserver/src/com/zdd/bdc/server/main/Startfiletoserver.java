package com.zdd.bdc.server.main;

import java.util.Date;
import java.util.concurrent.Executors;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Filetoserver;
import com.zdd.bdc.server.ex.Theserver;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/bigfiletoserver.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonclientlibs/bigconfigclient.jar:../../commonserverlibs/bigexserver.jar:../../commonclientlibs/bigexclient.jar com.zdd.bdc.server.main.Startfiletoserver pngbigto > log.runbigfiletoserver &
 */

public class Startfiletoserver {
	public static void main(String[] s) throws Exception {
		
		final String ip = Configclient.ip;

		final String port = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGDATA).read(STATIC.splitenc(STATIC.PARENTFOLDER, ip));

		Configclient.port = Integer.parseInt(port);
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.PARENTFOLDER + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(Executors.newCachedThreadPool(), ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, Configclient.shutdownifpending, 0, Filetoserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

	}

}
