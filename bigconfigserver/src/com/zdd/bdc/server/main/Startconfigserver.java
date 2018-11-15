package com.zdd.bdc.server.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.biz.Configserver;
import com.zdd.bdc.server.ex.Theserver;
import com.zdd.bdc.server.util.SS;


/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigconfigserver.jar:../../commonlibs/bigcomclientutil.jar:../../commonlibs/bigcomserverutil.jar:../../commonlibs/bigfileutil.jar:../../commonlibs/bigexserver.jar:../../commonlibs/bigexclient.jar com.zdd.bdc.server.main.Startconfigserver > log.runbigconfigserver &
 */

public class Startconfigserver {
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
		
		final String port = Configserver.readconfig(CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE, CS.REMOTE_CONFIGKEY_CONFIGSERVERPORT);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip, Integer.parseInt(port), SS.REMOTE_CONFIGVAL_PENDING, pending, 10, Configserver.class, null);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		
		while(!SS.REMOTE_CONFIGVAL_PENDING.equals(Configserver.readconfig(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING, CS.splitiport(ip, Configserver.readconfig(CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE, CS.REMOTE_CONFIGKEY_CONFIGSERVERPORT))))) {
			Thread.sleep(30000);
		}
		pending.append(SS.REMOTE_CONFIGVAL_PENDING);
		
		try {
			Theclient.request(ip, Integer.parseInt(port), null, null, null);//connect to make the socket server stop.
			System.out.println(new Date() + " ==== System exits and server stopped listening on ["+CS.splitiport(ip, port)+"]");
		}catch(Exception e) {
			//do nothing
		}
	}
}
