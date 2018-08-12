package com.zdd.bdc.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Configserver;
import com.zdd.bdc.ex.Theserver;
import com.zdd.bdc.util.STATIC;


/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigconfigserver.jar:../../commonlibs/bigcommonutil.jar:../../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter > log.runbigconfigserver &
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
					Theserver.startblocking(ip, Integer.parseInt(Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE, STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT)), STATIC.REMOTE_CONFIGVAL_PENDING, pending, 10, Configserver.class);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		
		while(!STATIC.REMOTE_CONFIGVAL_PENDING.equals(Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING, ip+STATIC.IP_SPLIT_PORT+Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE, STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT)))) {
			Thread.sleep(30000);
		}
		pending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		System.out.println(new Date()+" ==== System will exit when next connection attempts.");
	}
}
