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

public class Aexpire implements Filter {

	public static Map<String, Judge> JUDGES = new HashMap<String, Judge>();
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        if (req.getRequestURI().endsWith("html")||req.getRequestURI().endsWith("jpg")||req.getRequestURI().endsWith("png")||
        		req.getRequestURI().endsWith("gif")||req.getRequestURI().endsWith("js")||req.getRequestURI().endsWith("cg")||
        		req.getRequestURI().endsWith("cnt")||req.getRequestURI().endsWith("maintain")) {
	        	arg2.doFilter(req, res);
	        	return;
        }
        
	    	String judgeid = req.getParameter("judgeid");    
	    if (Judgepool.isexpire(judgeid)) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", "expire");
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
			return;
		}
			
		req.setAttribute("judgeid", judgeid);
		
		if (req.getParameter("playerid")!=null) {
			if (Judgepool.IDJUDGE.get(judgeid).getplayer(req.getParameter("playerid"))==null) {
				Map<String, String> result = new HashMap<String, String>();
				result.put("status", "yes");
				result.put("reason", "notjoined");
				res.getWriter().write(new ObjectMapper().writeValueAsString(result));
				return;
			}
    			req.setAttribute("playerid", req.getParameter("playerid"));
	    	}
		arg2.doFilter(req, res);
    }
	
}
