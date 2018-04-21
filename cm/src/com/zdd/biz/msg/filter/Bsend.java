package com.zdd.biz.msg.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.biz.msg.Longpollnotify;
import com.zdd.biz.msg.Msg;
import com.zdd.biz.msg.Msgpool;

public class Bsend implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
	        	if (Runtime.getRuntime().totalMemory()/Runtime.getRuntime().maxMemory()>0.8) {
	    			throw new Exception("full");
	    		}
        		Path msgfolder = Paths.get("msg");
			Files.createDirectories(msgfolder);
			
        		String groupid = req.getParameter("groupid");
        		String sessionid = req.getParameter("sessionid");
        		String senderid = req.getParameter("senderid");
        		if (sessionid==null||sessionid.trim().isEmpty()||senderid==null||senderid.trim().isEmpty()) {
        			throw new Exception("nosender");
        		}
        		if (groupid==null||groupid.trim().isEmpty()||!Files.exists(msgfolder.resolve(groupid))) {
        			throw new Exception("nogroup");
        		}
        		Msg m = null;
        		Map<String, Object> result = new HashMap<String, Object>();
    			result.put("status", "yes");
    			String msg = req.getParameter("msg");
    	    		if (msg!=null&&!msg.trim().isEmpty()) {
    	    			String groupurl = Files.readAllLines(msgfolder.resolve(groupid),Charset.forName("UTF-8")).get(0);
    	        		if (groupurl==null||groupurl.trim().isEmpty()) {
    	        			throw new Exception("nogroupurl");
    	        		}
    	        		String receivers = Request.Post(groupurl).connectTimeout(20000).socketTimeout(20000).bodyForm(Form.form().add("judgeid",sessionid).add("playerid", senderid).add("secret", "unehkeU$2qohr0175492de#").build()).execute().returnContent().asString();
    	        		JsonNode jn = new ObjectMapper().readTree(receivers);
    	        		if (jn.get("status").asText().equals("no")) {
    	        			throw new Exception(jn.get("reason").asText());
    	        		}
    	        		if (jn.get("receivers").size()==0) {
    	        			throw new Exception("noreceiver");
    	        		}
    	        		String sendernick = "";
    	        		for (int i=0;i<jn.get("receivers").size();i++) {
    					String receiverid = jn.get("receivers").get(i).get("id").asText();
    					if (senderid.equals(receiverid)) {
    						sendernick=jn.get("receivers").get(i).get("nick").asText();
    						break;
    					}
    	        		}	
    	    			m = new Msg(msg);
    	    			m.sendernick=sendernick;
    				for (int i=0;i<jn.get("receivers").size();i++) {
    					String receiverid = jn.get("receivers").get(i).get("id").asText();
    					if (Msgpool.receiveridmsgs.get(receiverid)==null) {
    						Msgpool.receiveridmsgs.put(receiverid,new ArrayList<Msg>());
    					} 
    					Msgpool.receiveridmsgs.get(receiverid).add(m);
    					Longpollnotify.donotify(receiverid);
    				}
    	    			result.put("received", jn.get("receivers").size());
        		}
    	    		res.getWriter().write(new ObjectMapper().writeValueAsString(result));
    	    	}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
	}
	
}
