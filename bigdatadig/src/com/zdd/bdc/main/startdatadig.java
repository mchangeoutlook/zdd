package com.zdd.bdc.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Digactive;
import com.zdd.bdc.biz.Sortingserver;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.ex.Theserver;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp bigtextdig.jar:../runbigdataserver/bigtextserver.jar:../../commonlibs/bigcommonutil.jar:../../commonlibs/bigexclient.jar:../../commonlibs/bigconfigclient.jar:../../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter unicorn > log.runbigtextdig &
 */

public class startdatadig {
	public static void main(String[] s) throws Exception {
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

		String dataserverport = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitenc(STATIC.PARENTFOLDER, ip));
		
		final String port = "1"+dataserverport;
		
		final StringBuffer pending = new StringBuffer();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip, Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending, 10, Sortingserver.class);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();
		
		int bigfilehash = Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitenc(ip, dataserverport)));
		
		new Digactive(ip, port, bigfilehash).start();
		
		while (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING).read(STATIC.splitenc(ip, port)))) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		pending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		System.out.println(new Date() + " ==== System will exit when next connection attempts.");

	}

}
