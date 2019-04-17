package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.entities.Yxyanxin;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/calendar")
public class Calendar extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			String year = null;
			try{
				int yearint = Integer.parseInt(request.getParameter("year"));
				if (yearint<2000||yearint>9999) {
					throw new Exception();
				}
				year = request.getParameter("year");
			}catch(Exception e) {
				year = Reuse.yyyyMMdd(new Date()).split("-")[0];
			}
			
			String month = null;
			try{
				int monthint = Integer.parseInt(request.getParameter("month"));
				if (monthint<1||monthint>12) {
					throw new Exception();
				}
				month = Reuse.yyyyMMdd(new SimpleDateFormat("MM").parse(request.getParameter("month"))).split("-")[1];
			}catch(Exception e) {
				month = Reuse.yyyyMMdd(new Date()).split("-")[1];
			}
			
			
			
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}