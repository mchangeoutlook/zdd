package com.zdd.biz.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Msgpool {
	public static Map<String, List<Msg>> receiveridmsgs = new HashMap<String, List<Msg>>();
	static {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						//do nothing
					}
					List<String> receiverids = new ArrayList<String>();
					for (Entry<String, List<Msg>> entry:receiveridmsgs.entrySet()) {
						receiverids.add(entry.getKey());
					}
					for (String receiverid:receiverids) {
						if (receiveridmsgs.get(receiverid)!=null&&!receiveridmsgs.get(receiverid).isEmpty()) {
							try {
								if (System.currentTimeMillis()-receiveridmsgs.get(receiverid).get(0).sendtime>300000) {
									receiveridmsgs.remove(receiverid);
								}
							}catch(Exception e) {
								//do nothing
							}
						}
					}
				}
				
			}
			
		}).start();
	}

}
