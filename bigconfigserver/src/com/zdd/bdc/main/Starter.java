package com.zdd.bdc.main;

import java.net.InetAddress;
import java.util.Date;

import com.zdd.bdc.biz.Configserver;
import com.zdd.bdc.ex.Theserver;

public class Starter {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		InetAddress iAddress = InetAddress.getLocalHost();
		final String ip = iAddress.getHostAddress();
		
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
