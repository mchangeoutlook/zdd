package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/write")
public class Write extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			//logout(yxlogin);
			newdayiscoming(yxlogin, false);
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	

}