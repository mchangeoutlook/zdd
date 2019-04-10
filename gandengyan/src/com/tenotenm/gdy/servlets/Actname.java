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
@WebServlet("/act/n")
public class Actname extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			String gameid = req.getParameter("gameid");
			String playerid = req.getParameter("playerid");
			String newname = req.getParameter("newname");
			Map<String, String> ret = new Hashtable<String, String>();
			ret.put("newname", Judges.get(gameid).actname(playerid, newname));
			Common.respond(res, ret, null);
		}catch(Exception e) {
			Common.respond(res, null, e);
		}
	}
}