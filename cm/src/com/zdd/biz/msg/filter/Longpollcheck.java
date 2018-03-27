package com.zdd.biz.msg.filter;

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
import com.zdd.biz.msg.Longpollnotify;
import com.zdd.util.Longpoll;

public class Longpollcheck implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
	        String receiverid = req.getParameter("receiverid");
	    		if (receiverid!=null&&!receiverid.trim().isEmpty()) {
	    			Longpoll.start(receiverid, 60000l, req, res);
	    			Longpollnotify.donotify(receiverid);
	    		} else {
	    			throw new Exception("receiverid");
	    		}
        }catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
    }
	
}

