package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/cexpired/readyanxin")
public class Readyanxin extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			Map<String, String> ret = Bizutil.convert(Bizutil.readyanxin(yxaccount, request.getParameter("yanxinkey")));
			if (ret.get("photo") != null && !ret.get("photo").trim().isEmpty()) {
				ret.put("onetimekeyphoto", Bizutil.onetimekey(null, ret.get("key")));
				ret.remove("photo");
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}