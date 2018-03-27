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
import com.zdd.biz.pe.Judge;
import com.zdd.biz.pe.Judgepool;
import com.zdd.biz.pe.Player;

public class Bprivate implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		String judgeid = (String)req.getAttribute("judgeid");
        		String playerid = (String)req.getAttribute("playerid");
        		if (playerid==null||playerid.trim().isEmpty()) {
        			throw new Exception("noplayerid");
        		}
        		if (!"unehkeU$2qohr0175492de#".equals(req.getParameter("secret"))) {
        			throw new Exception("denied");
        		}
        		Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			
			Judge j = Judgepool.IDJUDGE.get(judgeid);
			
			List<Map<String, Object>> players = new ArrayList<Map<String, Object>>();
			for (Player p:j.players()) {
				Map<String, Object> player = new HashMap<String, Object>();
				player.put("id", p.id());
				player.put("nick", p.nick);
				players.add(player);
			}
			result.put("receivers", players);
			
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
	}
	
}
