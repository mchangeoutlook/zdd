package com.zdd.biz.game10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Judgepool {
	public static final Map<String, Judge> IDJUDGE = new HashMap<String, Judge>();
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
					List<String> judgeids = new ArrayList<String>();
					for (Entry<String, Judge> entry:IDJUDGE.entrySet()) {
						judgeids.add(entry.getKey());
					}
					for (String judgeid:judgeids) {
						if (IDJUDGE.get(judgeid).expire()) {
							IDJUDGE.remove(judgeid);
						}
					}
				}
				
			}
			
		}).start();
	}
	public static boolean isexpire(String judgeid) {
		if (IDJUDGE.get(judgeid)!=null&&!IDJUDGE.get(judgeid).expire()) {
			return false;
		}
		return true;
	}
	
}
