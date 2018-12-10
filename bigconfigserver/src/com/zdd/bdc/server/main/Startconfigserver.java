package com.zdd.bdc.server.main;

import java.util.Date;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Configserver;
import com.zdd.bdc.server.ex.Theserver;


/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp ../../serverlibs/bigconfigserver.jar:../../commonclientlibs/bigcomclientutil.jar:../../commonserverlibs/bigcomserverutil.jar:../../commonserverlibs/bigfileutil.jar:../../commonserverlibs/bigexserver.jar:../../commonclientlibs/bigexclient.jar com.zdd.bdc.server.main.Startconfigserver > log.runbigconfigserver &
 */

public class Startconfigserver {
	public static void main(String[] s) throws Exception {
		
		final StringBuffer pending = new StringBuffer();
		
		final String ip = STATIC.localip();
		
		final String port = Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE, STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(STATIC.ES, ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending, 10, Configserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		
		while(!STATIC.REMOTE_CONFIGVAL_PENDING.equals(Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING, STATIC.splitiport(ip, Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE, STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT))))) {
			Thread.sleep(30000);
		}
		pending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		
		try {
			Theclient.request(ip, Integer.parseInt(port), null, null, null);//connect to make the socket server stop.
		}catch(Exception e) {
			//do nothing
		}
		STATIC.ES.shutdownNow();
		System.out.println(new Date() + " ==== System exits and server stopped listening on ["+STATIC.splitiport(ip, port)+"]");
	}
}
