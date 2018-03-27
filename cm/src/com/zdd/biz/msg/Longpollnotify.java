package com.zdd.biz.msg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.util.Longpoll;

public class Longpollnotify {
	
	public static synchronized void donotify(String receiverid) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "yes");
		List<Msg> toreturn = Msgpool.receiveridmsgs.get(receiverid);
		if (toreturn!=null&&!toreturn.isEmpty()) {
			result.put("msgs", toreturn);
			String out = new ObjectMapper().writeValueAsString(result);
			if (Longpoll.end(receiverid, out)) {
				JsonNode jn = new ObjectMapper().readTree(out);
				if (jn.get("msgs").size()>0) {
					for (int i=0;i<jn.get("msgs").size();i++) {
						if (Msgpool.receiveridmsgs.get(receiverid)!=null&&!Msgpool.receiveridmsgs.get(receiverid).isEmpty()) {
							Msgpool.receiveridmsgs.get(receiverid).remove(0);
						}
					}
				}
			}
		}
		
	}
}
