package com.zdd.biz.pe.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.biz.pe.Incard;
import com.zdd.biz.pe.Judge;
import com.zdd.biz.pe.Judgepool;

public class Bincard implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		String judgeid = (String)req.getAttribute("judgeid");
        		String playerid = (String)req.getAttribute("playerid");
        		String[] cardvalues = req.getParameterValues("cardvalues[]");
        		String[] playervalues = req.getParameterValues("playervalues[]");
        		
        		List<Integer> cardvals = new ArrayList<Integer>();
        		List<Integer> playervals = new ArrayList<Integer>();
        		
        		if (cardvalues!=null) {
        			for (String s:cardvalues) {
        				cardvals.add(Integer.parseInt(s));
        			}
        		}
        		
        		if (playervalues!=null) {
        			for (String s:playervalues) {
        				playervals.add(Integer.parseInt(s));
        			}
        		}
        		
        		Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			
			Judge j = Judgepool.IDJUDGE.get(judgeid);
			Incard incard = new Incard(playerid,j.players().indexOf(j.getplayer(playerid)));
			j.getplayer(playerid).autoplay=0;
			if (incard.valid(cardvals, playervals)){
				incard.nick=j.getplayer(playerid).nick;
				result.put("result", j.in(playerid, incard));
			} else {
				result.put("result", j.in(playerid, null));
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
