package com.zdd.biz.game10.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.biz.game10.Judgepool;
import com.zdd.biz.game10.Longpollnotify;
import com.zdd.util.Longpoll;

public class Longpollcheck implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
	        	String judgeid = (String)req.getAttribute("judgeid");
	    		String playerid = (String)req.getAttribute("playerid");
	    		if (playerid!=null) {
	    			Longpoll.start(playerid, 60000l, req, res);
	    			Longpollnotify.clear(playerid,req.getParameter("notifytime"));
	    			Longpollnotify.donotify(Judgepool.IDJUDGE.get(judgeid), null, playerid);
	    		} else {
	    			throw new Exception("noplayerid");
	    		}
        }catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
    }
	
}

