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
				throw new Exception("请填写账号");
			}
			Yxaccount yxaccount = new Yxaccount();
			yxaccount.setName(name);
			try {
				yxaccount.readunique(yxaccount.getUniquename());
			} catch (Exception e) {
				Bizutil.ipdeny(Reuse.getremoteip(request), true);
				throw new Exception("账号或密码或格言不正确，请重新登录");
			}
			
			Bizutil.refreshadminaccount(yxaccount);
			
			Bizutil.checkaccountreused(yxaccount);
			
			boolean isipuasame= true;
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
					isipuasame = false;
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
				} else {
					if (System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() < 60000) {
						throw new Exception("已启动账号保护，请在" + (60-(System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime())/1000)
								+ "秒后再来");
					}
				}
			}

			String pass = request.getParameter("pass");
			String motto = request.getParameter("motto");
			if ((pass==null||pass.trim().isEmpty())&&(motto==null||motto.trim().isEmpty())) {
				throw new Exception("请填写密码或格言之一");
			}
			if (pass != null && !pass.trim().isEmpty() && yxaccount.getPass().equals(Reuse.sign(pass)) || motto != null
					&& !motto.trim().isEmpty() && yxaccount.getMotto().equals(Reuse.sign(motto.toLowerCase().trim()))) {
				Yxlogin yxlogin = new Yxlogin();
				yxlogin.setIp(Reuse.getremoteip(request));
				yxlogin.setUa(Reuse.getuseragent(request));
				yxlogin.setYxaccountkey(yxaccount.getKey());
				yxlogin.create(null);
				yxaccount.setYxloginkey(yxlogin.getKey());
				yxaccount.modify(yxaccount.getKey());
				
				yxaccount.readunique(yxaccount.getUniquename());
				
				Map<String, Object> ret = new Hashtable<String, Object>();
				ret.put("loginkey", yxaccount.getYxloginkey());
				if (Bizutil.isaccountexpired(yxaccount)) {
					ret.put("isexpired", "t");
				} else {
					ret.put("isexpired", "f");
				}
				boolean canextend = new Date().after(new Date(yxaccount.getTimeexpire().getTime()-Reuse.getlongvalueconfig("extend.expire.in.days")*24*60*60*1000));
				if (canextend) {
					ret.put("expiretime", Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
				}
				ret.put("name", yxaccount.getName());
				ret.put("today", Reuse.yyyyMMdd(new Date()));
				Reuse.respond(response, ret, null);
			} else {
				if (!isipuasame) {
					if (System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() > Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")) {
						yxaccount.setTimewrongpass(new Date());
						yxaccount.modify(yxaccount.getKey());
					}
				} else {
					if (!yxaccount.getYxloginkey().isEmpty() &&System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() > 60000) {
						yxaccount.setTimewrongpass(new Date());
						yxaccount.modify(yxaccount.getKey());
					}
				}
				throw new Exception("密码或格言或账号不正确，请重新登录");
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
	
}