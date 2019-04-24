package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/changepass")
public class Changepass extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			String pass = request.getParameter("pass");
			String repass = request.getParameter("repass");
			String motto = request.getParameter("motto");
			String remotto = request.getParameter("remotto");
			if (pass == null && motto == null) {
				throw new Exception("缺少新密码或新格言");
			}
			if (Bizutil.setpassandmotto(yxaccount,  null, pass, repass, motto, remotto)) {
				yxaccount.modify(yxaccount.getKey());
			}
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}