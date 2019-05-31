package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yanxin;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/cexpired/write")
public class Write extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			
			String content = request.getParameter("content");
			if (content==null) {
				content = "";
			}
			String location = request.getParameter("location");
			if (location==null) {
				location = "";
			}
			String weather = request.getParameter("weather");
			if (weather==null) {
				weather = "";
			}
			String emotion = request.getParameter("emotion");
			if (emotion==null) {
				emotion = "";
			}
			Date today = new Date();
			
			Yanxin yx = Bizutil.readyanxin(yxaccount, today);
			if (yx==null) {
				yx = new Yanxin();
				yx.setKey(Bigclient.newbigdatakey());
				yx.setPhoto("");
				yx.setPhotosmall("");
				yx.setContent(content);
				yx.setLocation(location);
				yx.setWeather(weather);
				yx.setEmotion(emotion);
				yx.setUniquekeyprefix(yxaccount.getYxyanxinuniquekeyprefix());
				yx.setYxloginkey(yxlogin.getKey());
				yx.setTimecreate(new Date());
				try {
					yx.createunique(null, Bizutil.yanxinkey(yxaccount, today));
				}catch(Exception e) {
					if (e.getMessage()!=null&&e.getMessage().contains(STATIC.DUPLICATE)) {
						yx.readunique(Bizutil.yanxinkey(yxaccount, today));
						yx.setContent(content);
						yx.setLocation(location);
						yx.setWeather(weather);
						yx.setEmotion(emotion);
						yx.setYxloginkey(yxlogin.getKey());
						yx.modify(null);
					} else {
						throw e;
					}
				}
			} else {
				yx.setContent(content);
				yx.setLocation(location);
				yx.setWeather(weather);
				yx.setEmotion(emotion);
				yx.setYxloginkey(yxlogin.getKey());
				yx.modify(null);
			}

			Map<String, String> ret = Bizutil.convert(Bizutil.readyanxin(yxaccount, today));
			ret.remove("key");
			ret.remove("photo");
			ret.remove("day");
			String hint = Bizutil.newdaycominghint(yxlogin);
			if (hint!=null) {
				ret.put("newdaycomingminutes", hint);
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}


}