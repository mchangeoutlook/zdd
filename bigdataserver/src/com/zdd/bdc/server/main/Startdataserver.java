package com.zdd.bdc.server.main;

import java.util.Date;
import java.util.concurrent.Executors;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Dataserver;
import com.zdd.bdc.server.ex.Theserver;

public class Startdataserver {
	public static void main(String[] s) throws Exception {
		final String ip = Configclient.ip;
		final String port = Configclient.getinstance(STATIC.FOLDER_NAMESPACE, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitenc(STATIC.FOLDER_DATAPARENT, ip));
		Configclient.port = Integer.parseInt(port);
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.FOLDER_DATAPARENT + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitiport(ip, port)));
					Theserver.startblocking(Executors.newCachedThreadPool(), ip,
							Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, Configclient.shutdownifpending,
							bigfilehash,
							 Dataserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		
	}
}
