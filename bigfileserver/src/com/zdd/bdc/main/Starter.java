package com.zdd.bdc.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Enumeration;

import com.zdd.bdc.biz.Configclient;
import com.zdd.bdc.biz.Filefromserver;
import com.zdd.bdc.ex.Theserver;

/**
 * @author mido how to run: nohup /data/jdk-9.0.4/bin/java -cp bigfilefromserver.jar:../commonlibs/bigcommonutil.jar:../commonlibs/bigexclient.jar:../commonlibs/bigconfigclient.jar:../commonlibs/bigexserver.jar com.zdd.bdc.main.Starter > log.runbigfilefromserver &
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
		if (localip == null) {
			localip = InetAddress.getLocalHost().getHostAddress();
		}
		final String ip = localip;

		String parentfolder = Paths.get(".").toAbsolutePath().getParent().getFileName().toString();

		final String port = Configclient.getinstance("pngbigfrom", "bigdata").read(parentfolder + ":" + ip);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(ip, Integer.parseInt(port), pending, 10, Filefromserver.class);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

		while (!"pending".equals(Configclient.getinstance("core", "pending").read(ip + ":" + port))) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		pending.append("pending");
		System.out.println(new Date() + " ==== System will exit when next connection attempts.");

	}

}
