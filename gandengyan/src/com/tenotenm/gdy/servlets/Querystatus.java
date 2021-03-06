package com.tenotenm.gdy.servlets;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.gdy.util.Common;
import com.tenotenm.gdy.util.Judges;

@SuppressWarnings("serial")
@WebServlet("/qs")
public class Querystatus extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		try {
			String gameid = req.getParameter("gameid");
			String playerid = req.getParameter("playerid");
			String round = req.getParameter("round");
			String queueindex = req.getParameter("queueindex");
			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("statuslist", Judges.get(gameid).querystatus(playerid, Long.parseLong(round), Integer.parseInt(queueindex)));
			Common.respond(res, ret, null);
		}catch(Exception e) {
			Common.respond(res, null, e);
		}
	}
}