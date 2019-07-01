package com.zdd.bdc.main;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;

import com.zdd.bdc.biz.Digactive;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.sort.distribute.Sortserver;

public class Startdatadig {
	
	public static String sortserverport(String dataserverport) {
		return "1"+dataserverport;
	}
	
	public static void main(String[] s) throws Exception {
		
		final String ip = Configclient.ip;

		String dataserverport = Configclient.getinstance(STATIC.FOLDER_NAMESPACE, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitenc(STATIC.FOLDER_DATAPARENT, ip)+STATIC.REMOTE_CONFIGKEY_SERVERPORTSUFFIX);
		
		final String port = sortserverport(dataserverport);
		
		Configclient.port = Integer.parseInt(port);
		
		System.out.println(new Date()+" ==== starting in folder ["+STATIC.FOLDER_DATAPARENT + "]");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> additionalserverconfig = new Hashtable<String, Object>();
					additionalserverconfig.put(Sortserver.sortcheckclasskey,"com.zdd.bdc.biz.Sortcheckimpl");
					
					int bigfilehash = Integer.parseInt(Configclient.getinstance(STATIC.FOLDER_NAMESPACE, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitiport(ip, dataserverport)+STATIC.REMOTE_CONFIGKEY_SERVERPORTSUFFIX));
					
					Theserver.startblocking(Executors.newCachedThreadPool(), ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, Configclient.shutdownifpending, bigfilehash, Sortserver.class, additionalserverconfig);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		int bigfilehash = Integer.parseInt(Configclient.getinstance(STATIC.FOLDER_NAMESPACE, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitiport(ip, dataserverport)+STATIC.REMOTE_CONFIGKEY_SERVERPORTSUFFIX));
		
		new Digactive(ip, port, STATIC.FOLDER_NAMESPACE, bigfilehash).start();
		
	}

}
