package com.zdd.biz.pe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.util.Longpoll;

public class Judgepool {
	public static final Map<String, Judge> IDJUDGE = new HashMap<String, Judge>();
	static {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						//do nothing
					}
					List<String> judgeids = new ArrayList<String>();
					for (Entry<String, Judge> entry:IDJUDGE.entrySet()) {
						judgeids.add(entry.getKey());
					}
					for (String judgeid:judgeids) {
						if (IDJUDGE.get(judgeid).expire()) {
							for (Player p:IDJUDGE.get(judgeid).players()) {
								Map<String, Object> tonotify = new HashMap<String, Object>();
								tonotify.put("status", "no");
								tonotify.put("reason", "expire");
								try {
									Longpoll.end(p.id(), new ObjectMapper().writeValueAsString(tonotify));
								} catch (Exception e) {
									//do nothing
								}
							}
							IDJUDGE.remove(judgeid);
						} else if (IDJUDGE.get(judgeid).round>0){
							try {
								Integer autoplay = 0;
								for (Player p: IDJUDGE.get(judgeid).players()) {
									autoplay+=p.autoplay;
								}
								if (autoplay!=IDJUDGE.get(judgeid).players().size()) {
									IDJUDGE.get(judgeid).auto();
								}
							} catch (Exception e) {
								//do nothing
							}
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
