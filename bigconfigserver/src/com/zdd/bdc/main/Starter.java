package com.zdd.bdc.main;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Configserver;
import com.zdd.bdc.ex.Theserver;


/**
 * @author mido
 * how to run: 
 * nohup /data/jdk-9.0.4/bin/java -cp bigconfigserver.jar:../commonlibs/bigcommonutil.jar:../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter > log.runbigconfigserver &
 */

public class Starter {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		NetworkInterface ni = NetworkInterface.getByName("eth0");
        Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();

        String localip = null;
        while(inetAddresses.hasMoreElements()) {
            InetAddress ia = inetAddresses.nextElement();
            if(!ia.isLinkLocalAddress()) {
                localip = ia.getHostAddress();
                break;
            }
        }
		final String ip = localip;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip, Integer.parseInt(Configserver.read("core", "core", "configserverport")), pending, 10, Configserver.class);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		
		while(!"pending".equals(Configserver.read("core", "pending", ip+"."+Configserver.read("core", "core", "configserverport")))) {
			Thread.sleep(30000);
		}
		pending.append("pending");
		System.out.println(new Date()+" ==== System will exit when next connection attempts.");
	}
}
