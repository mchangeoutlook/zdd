package com.tenotenm.yanxin.servlets;

import java.io.IOException;
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
public class Extendexpire extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			//extend.expire.in.days
			//extend.expire.days.max
			Reuse.getlongconfig("configkey");
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Date day = new Date();
			try {
				day = Reuse.yyyyMMdd(request.getParameter("ymd"));
			}catch(Exception e) {
				//do nothing
			}
			Map<String, String> ret = new Hashtable<String, String>();
			ret.put("istoday", "f");
			Yxyanxin yxyanxin = new Yxyanxin();
			try {
				yxyanxin.readunique(yxaccount.getYxyanxinuniquekeyprefix()+"-"+Reuse.yyyyMMdd(day));
			}catch(Exception e) {
				if (Reuse.yyyyMMdd(day).equals(Reuse.yyyyMMdd(new Date()))) {
					yxyanxin.setContent("");
					yxyanxin.setLocation("");
					yxyanxin.setPhoto("");
					yxyanxin.setWeather("");
					yxyanxin.setYxloginkey(yxlogin.getKey());
					yxyanxin.createunique(null, yxaccount.getYxyanxinuniquekeyprefix()+"-"+Reuse.yyyyMMdd(day));
					ret.put("istoday", "t");
				} else {
					throw new Exception(Reuse.yyyyMMdd(day)+"未写日记");
				}
			}
			ret.put("content", yxyanxin.getContent());
			ret.put("key", yxyanxin.getKey());
			ret.put("location", yxyanxin.getLocation());
			ret.put("photo", yxyanxin.getPhoto());
			ret.put("weather", yxyanxin.getWeather());
			ret.put("timecreate", Reuse.yyyyMMdd(yxyanxin.getTimecreate()));
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}