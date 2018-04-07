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

public class Bcreategame implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		Map<String, String> result = new HashMap<String, String>();
			result.put("status", "yes");
			Judge j = new Judge();
			j.settingadvance = 0;
			if ("1".equals(req.getParameter("settingadvance"))) {
				j.settingadvance = 1;
			} else if ("2".equals(req.getParameter("settingadvance"))) {
				j.settingadvance = 2;
			} else if ("3".equals(req.getParameter("settingadvance"))) {
				j.settingadvance = 3;
			}
			String playerid = j.joinordispatchcard(null, req.getParameter("nick"));
    		 	Judgepool.IDJUDGE.put(j.id, j);
			result.put("judgeid", j.id);
			result.put("playerid", playerid);
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
	}
	
}
