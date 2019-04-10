package com.tenotenm.gdy.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.gdy.game.Judge;
import com.tenotenm.gdy.util.Common;
import com.tenotenm.gdy.util.Judges;

@WebFilter("/*")
public class General implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse res = (HttpServletResponse) arg1;
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String gameid=req.getParameter("gameid");
			if (!Common.isvalid(gameid)) {
				Common.respond(res, new Judge().creategame(), null);
			} else {
				if (Judges.get(gameid)==null) {
					throw new Exception("无效游戏编号["+gameid+"]");
				} else {
					String playerid = req.getParameter("playerid");
					if (Common.isvalid(playerid)&&Judges.get(gameid).getplayerbyid(playerid)==null) {
						throw new Exception("无效玩家编号["+playerid+"]");
					} else {
						if (req.getRequestURI().contains("/act/")&&!Common.isvalid(playerid)) {
							throw new Exception("观棋不语真君子");
						} else {
							arg2.doFilter(req, res);
						}
					}
				}
			}
		}catch(Exception e) {
			Common.respond(res, null, e);
		}
	}

}
