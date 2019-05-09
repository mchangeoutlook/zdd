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
@WebServlet("/creused/changepass")
public class Changepass extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			String pass = request.getParameter("pass");
			String repass = request.getParameter("repass");
			if (pass == null) {
				throw new Exception("缺少密码");
			}
			if (!pass.equals(repass)) {
				throw new Exception("两次输入的密码不一致");
			}
			yxaccount.setPass(pass);
			yxaccount.setTimeupdate(new Date());
			yxaccount.modify(yxaccount.getKey());
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}