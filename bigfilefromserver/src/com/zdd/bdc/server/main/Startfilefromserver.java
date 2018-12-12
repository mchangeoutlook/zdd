package com.zdd.bdc.server.main;

import java.util.Date;
import java.util.concurrent.Executors;

import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.server.biz.Filefromserver;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/bigfilefromserver.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonclientlibs/bigexclient.jar:../../commonclientlibs/bigconfigclient.jar:../../commonserverlibs/bigexserver.jar:../../commonclientlibs/bigexclient.jar com.zdd.bdc.server.main.Startfilefromserver pngbigfrom > log.runbigfilefromserver &
 */

public class Startfilefromserver {
	public static void main(String[] s) throws Exception {
		
		final String ip = Configclient.ip;

		final String port = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGDATA).read(STATIC.splitenc(STATIC.PARENTFOLDER, ip));
		
		Configclient.port = Integer.parseInt(port);
		
		final StringBuffer pending = new StringBuffer();
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.PARENTFOLDER + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(Executors.newCachedThreadPool(), ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending, 10, Filefromserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

		while (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING).read(STATIC.splitiport(ip, port)))) {
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
