package com.zdd.biz.game10.filter;

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
import com.zdd.biz.game10.Judge;
import com.zdd.biz.game10.Judgepool;
import com.zdd.biz.game10.Player;

public class Bgameinfo implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        try {
        		String judgeid = (String)req.getAttribute("judgeid");
        		String playerid = (String)req.getAttribute("playerid");
        		
        		Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			
			Judge j = Judgepool.IDJUDGE.get(judgeid);
			
			result.put("judge", j);

			List<Map<String, Object>> players = new ArrayList<Map<String, Object>>();
			for (Player p:j.players()) {
				Map<String, Object> player = new HashMap<String, Object>();
				if (p.id().equals(playerid)) {
					player.put("cards", p.mycards);
					player.put("meindex", p.index);
				}
				if (p.index==j.nextplayerindex) {
					player.put("meplay", p.index);
				}
				player.put("index", p.index);
				player.put("nick", p.nick);
				player.put("wins", p.wins);
				player.put("rest", p.mycards.size());
				players.add(player);
			}
			result.put("players", players);
			
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
	}
	
}
