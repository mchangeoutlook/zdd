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
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/freego/login")
public class Login extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) {
				throw new Exception("缺少账号名称");
			}
			name = name.toLowerCase().trim();
			Yxaccount yxaccount = new Yxaccount();
			try {
				yxaccount.readunique(name);
			} catch (Exception e) {
				Bizutil.ipdeny(Reuse.getremoteip(request), true);
				throw new Exception("账号或密码或格言不正确，请重新登录");
			}
			
			Bizutil.refreshaccount(yxaccount, null, null, null, null, null, null, null, null);
			
			Bizutil.checkaccountreused(yxaccount);
			
			if (yxaccount.getYxloginkey().isEmpty()) {// first time login
				if (!yxaccount.getIp().equals(Reuse.getremoteip(request))
						|| !yxaccount.getUa().equals(Reuse.getuseragent(request))) {
					Bizutil.ipdeny(Reuse.getremoteip(request), true);
					throw new Exception("请使用注册时的IP和浏览器完成首次登录");
				}
			} else {
				Yxlogin yxlogin = new Yxlogin();
				yxlogin.read(yxaccount.getYxloginkey());
				if (!yxlogin.getIp().equals(Reuse.getremoteip(request))
						|| !yxlogin.getUa().equals(Reuse.getuseragent(request))) {
					if (System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() < Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")) {
						throw new Exception("已启动账号保护，请" + Reuse.yyyyMMddHHmmss(new Date(yxaccount.getTimewrongpass().getTime()
								+ Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")))
								+ "后再来");
					}
					boolean isexpire = System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Reuse.getsecondsmillisconfig("session.expire.seconds");
					if (!yxlogin.getIslogout() && !isexpire) {
						throw new Exception("已在其它地方登录，请退出其它地方的登录或"
								+ Reuse.yyyyMMddHHmmss(new Date(yxlogin.getTimeupdate().getTime() + Reuse.getsecondsmillisconfig("session.expire.seconds")))
								+ "后再来");
					}
				}
				Bizutil.logout(yxlogin);
			}

			String pass = request.getParameter("pass");
			String motto = request.getParameter("motto");
			if (pass != null && !pass.trim().isEmpty() && yxaccount.getPass().equals(Reuse.sign(pass)) || motto != null
					&& !motto.trim().isEmpty() && yxaccount.getMotto().equals(Reuse.sign(motto.toLowerCase().trim()))) {
				Yxlogin yxlogin = new Yxlogin();
				yxlogin.setIp(Reuse.getremoteip(request));
				yxlogin.setUa(Reuse.getuseragent(request));
				yxlogin.setYxaccountkey(yxaccount.getKey());
				yxlogin.create(null);
				yxaccount.setYxloginkey(yxlogin.getKey());
				yxaccount.modify(yxaccount.getKey());
				
				yxaccount = new Yxaccount();
				yxaccount.readunique(name);
				
				
				Map<String, Object> ret = new Hashtable<String, Object>();
				ret.put("daystogive", yxaccount.getDaystogive());
				ret.put("timecreate", Reuse.yyyyMMddHHmmss(yxaccount.getTimecreate()));
				ret.put("timeexpire", Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
				ret.put("timereuse", Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(yxaccount)));
				ret.put("loginkey", yxaccount.getYxloginkey());
				ret.put("today", Reuse.yyyyMMdd(new Date()));
				if (Bizutil.isadmin(yxaccount)) {
					ret.put("isadmin", "t");
				} else {
					ret.put("isadmin", "f");
				}
				if (Bizutil.isaccountexpired(yxaccount)) {
					ret.put("isexpired", "t");
				} else {
					ret.put("isexpired", "f");
				}
				ret.put("name", yxaccount.getUniquename());
				Reuse.respond(response, ret, null);
			} else {
				if (System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() > Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")) {
					yxaccount.setTimewrongpass(new Date());
					yxaccount.modify(yxaccount.getKey());
				}
				throw new Exception("密码或格言或账号不正确，请重新登录");
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
	
}