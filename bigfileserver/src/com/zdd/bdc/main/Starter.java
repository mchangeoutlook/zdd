package com.zdd.bdc.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Configclient;
import com.zdd.bdc.biz.Filefromserver;
import com.zdd.bdc.biz.Filetoserver;
import com.zdd.bdc.ex.Theserver;

/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigfileserver.jar:../commonlibs/bigcommonutil.jar:../commonlibs/bigexclient.jar:../commonlibs/bigconfigclient.jar:../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter > log.runbigfileserver &
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

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip,
							Integer.parseInt(Configclient.getinstance("core", "core").read("fileserverport")), pending,
							10,
							Filefromserver.class);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip,
							Integer.parseInt(Configclient.getinstance("core", "core").read("fileserverport")), pending,
							10,
							Filetoserver.class);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

		while (!"pending".equals(Configclient.getinstance("core", "pending")
				.read(ip + "." + Configclient.getinstance("core", "core").read("fileserverport")))) {
			Thread.sleep(30000);
		}
		pending.append("pending");
		System.out.println(new Date() + " ==== System will exit when next connection attempts.");
	}

}
