package com.zdd.bdc.main;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Digactive;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.sort.distribute.Sortserver;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/bigdatadig.jar:../../serverlibs/bigdataserver.jar:../../commonserverlibs/bigsort.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonclientlibs/bigexclient.jar:../../commonclientlibs/bigconfigclient.jar:../../commonclientlibs/bigindexclient.jar:../../commonclientlibs/bigdataclient.jar:../../commonserverlibs/bigexserver.jar com.zdd.bdc.main.Startdatadig unicorn > log.runbigdatadig &
 */

public class Startdatadig {
	
	public static String sortserverport(String dataserverport) {
		return "1"+dataserverport;
	}
	
	public static void main(String[] s) throws Exception {
		
		final String ip = Configclient.ip;

		String dataserverport = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGDATA).read(STATIC.splitenc(STATIC.PARENTFOLDER, ip));
		
		final String port = sortserverport(dataserverport);
		
		Configclient.port = Integer.parseInt(port);
		
		final StringBuffer pending = new StringBuffer();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> additionalserverconfig = new Hashtable<String, Object>();
					additionalserverconfig.put(Sortserver.sortcheckclasskey,"com.zdd.bdc.biz.Sortcheckimpl");
					
					int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGDATA).read(STATIC.splitiport(ip, dataserverport)));
					
					Theserver.startblocking(STATIC.ES, ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending, bigfilehash, Sortserver.class, additionalserverconfig);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIG_BIGDATA).read(STATIC.splitiport(ip, dataserverport)));
		
		new Digactive(ip, port, s[0], bigfilehash).start();
		
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
