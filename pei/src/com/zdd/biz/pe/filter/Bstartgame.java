package com.zdd.biz.pe.filter;

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
import com.zdd.biz.pe.Judge;
import com.zdd.biz.pe.Judgepool;

public class Bstartgame implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		String judgeid = (String)req.getAttribute("judgeid");
        		String playerid = (String)req.getAttribute("playerid");
        		
        		Map<String, String> result = new HashMap<String, String>();
			result.put("status", "yes");
			
			Judge j = Judgepool.IDJUDGE.get(judgeid);
			if (playerid!=null) {
				j.getplayer(playerid).autoplay=0;
				j.joinordispatchcard(playerid, null);
			} else {
				result.put("playerid", j.joinordispatchcard(null, req.getParameter("nick")));
				result.put("judgeid",judgeid);
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
