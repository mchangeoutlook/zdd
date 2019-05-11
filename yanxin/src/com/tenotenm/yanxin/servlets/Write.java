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
			} else {
				if (content.getBytes("UTF-8").length>3000) {
					throw new Exception("内容过长");
				}
			}
			String location = request.getParameter("location");
			if (location==null) {
				location = "";
			} else {
				if (location.getBytes("UTF-8").length>300) {
					throw new Exception("地址过长");
				}
			}
			String weather = request.getParameter("weather");
			if (weather==null) {
				weather = "";
			} else {
				if (weather.getBytes("UTF-8").length>30) {
					throw new Exception("天气过长");
				}
			}
			Date today = new Date();
			
			Yanxin yx = Bizutil.readyanxin(yxaccount, today);
			if (yx==null) {
				yx = new Yanxin();
				yx.setKey(Bigclient.newbigdatakey());
				yx.setPhoto("");
				yx.setContent(content);
				yx.setLocation(location);
				yx.setWeather(weather);
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
						yx.setYxloginkey(yxlogin.getKey());
						yx.modify(null);
					} else {
						throw e;
					}
				}
			}

			Map<String, Object> ret = new Hashtable<String, Object>();
			Integer m = Bizutil.newdaycomingminutes(yxlogin);
			if (m!=null) {
				ret.put("newdaycomingminutes", "健康的身体需要充足的睡眠，请尽快结束今天的日记，"+m+"分钟后你将不能继续修改今天的日记，零点后你需要重新登录并开启新一天的日记");
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}


}