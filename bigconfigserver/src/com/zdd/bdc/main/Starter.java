package com.zdd.bdc.main;

import java.util.Date;

import com.zdd.bdc.biz.Configserver;
import com.zdd.bdc.ex.Theserver;

public class Starter {
	public static void main(String[] s) throws Exception {
		final StringBuffer pending = new StringBuffer();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Theserver.startblocking(Integer.parseInt(Configserver.read("core", "core", "configserverport")), pending, 10, Configserver.class);
				} catch (Exception e) {
					System.out.println(new Date()+" ==== System exit due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		}).start();
		//Thread.sleep(20000);
		//pending.append("pending");
	}
}
