package com.zdd.bdc.main;

import java.net.InetAddress;
import java.util.Date;

import com.zdd.bdc.biz.Configclient;
import com.zdd.bdc.biz.Indexserver;
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
					Theserver.startblocking(ip,
							Integer.parseInt(Configclient.getinstance("core", "core").read("indexserverport")), pending,
							Integer.parseInt(Configclient.getinstance("core", "core").read("indexfilehash")),
							Indexserver.class);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}

		}).start();

		while (!"pending".equals(Configclient.getinstance("core", "pending")
				.read(ip + "." + Configclient.getinstance("core", "core").read("indexserverport")))) {
			Thread.sleep(30000);
		}
		pending.append("pending");
		System.out.println(new Date() + " ==== System will exit when next connection attempts.");
	}

}
