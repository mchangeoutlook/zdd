package com.zdd.bdc.main;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Digactive;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.server.util.SS;
import com.zdd.bdc.sort.distribute.Sortserver;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp ../../severlibs/bigdatadig.jar:../../serverlibs/bigdataserver.jar:../../commonserverlibs/bigsort.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonclientlibs/bigexclient.jar:../../commonclientlibs/bigconfigclient.jar:../../commonserverlibs/bigexserver.jar com.zdd.bdc.main.Startdatadig unicorn > log.runbigdatadig &
 */

public class Startdatadig {
	
	public static String sortserverport(String dataserverport) {
		return "1"+dataserverport;
	}
	
	public static void main(String[] s) throws Exception {
		
		final String ip = Configclient.ip;

		String dataserverport = Configclient.getinstance(s[0], CS.REMOTE_CONFIG_BIGDATA).read(CS.splitenc(SS.PARENTFOLDER, ip));
		
		final String port = sortserverport(dataserverport);
		
		Configclient.port = Integer.parseInt(port);
		
		final StringBuffer pending = new StringBuffer();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> additionalserverconfig = new Hashtable<String, Object>();
					additionalserverconfig.put(Sortserver.sortcheckclasskey,"com.zdd.bdc.biz.Sortcheckimpl");
					Theserver.startblocking(ip, Integer.parseInt(port), SS.REMOTE_CONFIGVAL_PENDING, pending, 10, Sortserver.class, additionalserverconfig);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], CS.REMOTE_CONFIG_BIGDATA).read(CS.splitenc(ip, dataserverport)));
		
		new Digactive(ip, port, bigfilehash).start();
		
		while (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(CS.splitenc(ip, port)))) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		pending.append(SS.REMOTE_CONFIGVAL_PENDING);
		
		Configclient.running = false;
		
		try {
			Theclient.request(ip, Integer.parseInt(port), null, null, null);//connect to make the socket server stop.
			System.out.println(new Date() + " ==== System exits and server stopped listening on ["+CS.splitiport(ip, port)+"]");
		}catch(Exception e) {
			//do nothing
		}

	}

}
