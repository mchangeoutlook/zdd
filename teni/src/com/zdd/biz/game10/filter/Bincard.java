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
import com.zdd.biz.game10.Judge;
import com.zdd.biz.game10.Judgepool;

public class Bincard implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		String judgeid = (String)req.getAttribute("judgeid");
        		String playerid = (String)req.getAttribute("playerid");
        		Integer card = Integer.parseInt(req.getParameter("card"));
        		Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			
			Judge j = Judgepool.IDJUDGE.get(judgeid);
			j.in(playerid, card);
			
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
	}
	
}
