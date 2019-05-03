package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;
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
@WebServlet("/check/readtoday")
public class Readtoday extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Bizutil.checkaccountavailability(yxaccount);
			Date today = new Date();
			Map<String, String> ret = Bizutil.convert(Bizutil.readyanxin(yxaccount, today));
			ret.put("today", Reuse.yyyyMMdd(today));
			Reuse.respond(response, ret, null);

		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}