package com.zdd.bdc.server.main;

import java.util.Date;
import java.util.concurrent.Executors;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Uniqueindexserver;
import com.zdd.bdc.server.ex.Theserver;

/**
 * @author mido how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/biguniqueindexserver.jar:../../commonclientlibs/bigexclient.jar:../../commonclientlibs/bigconfigclient.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonserverlibs/bigexserver.jar com.zdd.bdc.server.main.Startuniqueindexserver unicorn servergroups0 > log.runbiguniqueindexserver &
 */
public class Startuniqueindexserver {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		
		final String ip = Configclient.ip;
		String p = null;
		int uniqueindexservers = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX)
				.read(s[1]));
		String current = STATIC.splitenc(STATIC.PARENTFOLDER, ip);
		for (int i = 0;i<uniqueindexservers;i++) {
			if (Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX)
				.read(s[1]+i).startsWith(current)) {
				p = STATIC.splitenc(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX)
				.read(s[1]+i))[2];
				break;
			}
		}
		final String port = p;
		Configclient.port = Integer.parseInt(port);
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.PARENTFOLDER + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					
					Theserver.startblocking(Executors.newCachedThreadPool(), ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending,
							-1,
							Uniqueindexserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== Uniqueindexserver exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
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
