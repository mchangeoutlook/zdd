package com.zdd.bdc.server.main;

import java.util.Date;
import java.util.concurrent.Executors;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Pagedindexserver;
import com.zdd.bdc.server.biz.Movingpageddistribution;
import com.zdd.bdc.server.ex.Theserver;

/**
 * @author mido how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/bigpagedindexserver.jar:../../commonclientlibs/bigpagedindexclient.jar:../../commonclientlibs/bigconfigclient.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonserverlibs/bigexserver.jar:../../commonclientlibs/bigexclient.jar com.zdd.bdc.server.main.Startpagedindexserver unicorn > log.runbigpagedindexserver &
 */
public class Startpagedindexserver {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		
		final String ip = Configclient.ip;
		final String port = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGPAGEDINDEX)
				.read(STATIC.splitenc(STATIC.PARENTFOLDER, ip));
		Configclient.port = Integer.parseInt(port);
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.PARENTFOLDER + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGPAGEDINDEX).read(STATIC.splitiport(ip, port)));
					
					Theserver.startblocking(Executors.newCachedThreadPool(), ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending,
							bigfilehash,
							Pagedindexserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== Indexserver exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		new Movingpageddistribution(ip, port).start();
		
		while (!STATIC.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING)
						.read(STATIC.splitiport(ip, port)))) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		
		pending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		
		Configclient.running = false;

		try {
			Theclient.request(ip, Integer.parseInt(port), null, null, null);//connect to make the socket server stop.
		}catch(Exception e) {
			//do nothing
		}
		
		STATIC.ES.shutdownNow();
		
		System.out.println(new Date() + " ==== System exits and server stopped listening on ["+STATIC.splitiport(ip, port)+"]");
	}

}
