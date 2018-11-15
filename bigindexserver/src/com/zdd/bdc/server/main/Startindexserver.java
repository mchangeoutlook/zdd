package com.zdd.bdc.server.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.biz.Indexserver;
import com.zdd.bdc.server.biz.Movingdistribution;
import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.server.util.SS;

/**
 * @author mido how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigindexserver.jar:../../commonlibs/bigindexclient.jar:../../commonlibs/bigexclient.jar:../../commonlibs/bigconfigclient.jar:../../commonlibs/bigcomclientutil.jar:../../commonlibs/bigcomserverutil.jar:../../commonlibs/bigexserver.jar:../../commonlibs/bigexclient.jar com.zdd.bdc.server.main.Startindexserver unicorn > log.runbigindexserver &
 */
public class Startindexserver {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		String localip = null;
		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						localip = addr.getHostName();
						break;
					}
				}
			}
		}
		if (localip == null) {
			localip = InetAddress.getLocalHost().getHostAddress();
		}
		final String ip = localip;
		final String port = Configclient.getinstance(s[0], CS.REMOTE_CONFIG_BIGINDEX)
				.read(CS.splitenc(SS.PARENTFOLDER, ip));
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], CS.REMOTE_CONFIG_BIGINDEX).read(CS.splitiport(ip, port)));
					
					Theserver.startblocking(ip, Integer.parseInt(port), SS.REMOTE_CONFIGVAL_PENDING, pending,
							bigfilehash,
							Indexserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== Indexserver exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		new Movingdistribution(ip, port).start();
		
		while (!SS.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
						.read(CS.splitiport(ip, port)))) {
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
