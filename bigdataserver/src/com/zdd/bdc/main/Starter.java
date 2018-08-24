package com.zdd.bdc.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Configclient;
import com.zdd.bdc.biz.Textserver;
import com.zdd.bdc.ex.Theserver;
import com.zdd.bdc.util.STATIC;

/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigtextserver.jar:../../commonlibs/bigexclient.jar:../../commonlibs/bigconfigclient.jar:../../commonlibs/bigcommonutil.jar:../../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter unicorn > log.runbigtextserver &
 */

public class Starter {
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
        if (localip==null) {
        		localip = InetAddress.getLocalHost().getHostAddress();
        }
		final String ip = localip;
		final String port = Configclient.getinstance(s[0], STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.PARENTFOLDER + STATIC.SPLIT_IP_PORT + ip);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip,
							Integer.parseInt(port), STATIC.REMOTE_CONFIGVAL_PENDING, pending,
							Integer.parseInt(Configclient.getinstance(s[0], STATIC.REMOTE_CONFIGFILE_BIGDATA).read(ip + STATIC.SPLIT_IP_PORT + port)),
							 Textserver.class);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		while (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING).read(ip + STATIC.SPLIT_IP_PORT + port))) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		pending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		System.out.println(new Date()+" ==== System will exit when next connection attempts.");
	}
}
