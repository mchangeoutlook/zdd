package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/changemotto")
public class Changemotto extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			String motto = request.getParameter("motto");
			String remotto = request.getParameter("remotto");
			if (motto == null) {
				throw new Exception("缺少格言");
			}
			if (!motto.toLowerCase().trim().equals(remotto.toLowerCase().trim())) {
				throw new Exception("两次输入的格言不一致");
			}
			yxaccount.setMotto(motto);
			yxaccount.setTimeupdate(new Date());
			yxaccount.modify(yxaccount.getKey());
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}