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
import com.tenotenm.yanxin.filters.Check;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

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
				Check.ipdeny(Reuse.getremoteip(request), true);
				throw new Exception("账号或密码或格言不正确，请重新登录");
			}
			
			if (yxaccount.getYxloginkey().isEmpty()) {// first time login
				if (!yxaccount.getIp().equals(Reuse.getremoteip(request))
						|| !yxaccount.getUa().equals(Reuse.getuseragent(request))) {
					Check.ipdeny(Reuse.getremoteip(request), true);
					throw new Exception("请使用注册时的IP和浏览器完成首次登录");
				}
			} else {
				Yxlogin yxlogin = new Yxlogin();
				yxlogin.read(yxaccount.getYxloginkey());
				if (!yxlogin.getIp().equals(Reuse.getremoteip(request))
						|| !yxlogin.getUa().equals(Reuse.getuseragent(request))) {
					if (System.currentTimeMillis() - yxaccount.getTimewrongpass().getTime() < Long.parseLong(Configclient
							.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("wrongpasswaitseconds"))
							* 1000) {
						throw new Exception("已启动账号保护，请" + Reuse.yyyymmddhhmmss(new Date(yxaccount.getTimewrongpass().getTime()
								+ Long.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
										.read("wrongpasswaitseconds")) * 1000))
								+ "后再来");
					}
					boolean isexpire = System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Long
							.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
									.read("sessionexpireseconds"))
							* 1000;
					if (!yxlogin.getIslogout() && !isexpire) {
						Check.ipdeny(Reuse.getremoteip(request), true);
						throw new Exception("请退出其它地方的登录或"
								+ Reuse.yyyymmddhhmmss(new Date(yxlogin.getTimeupdate().getTime() + Long.parseLong(
										Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
												.read("sessionexpireseconds"))
										* 1000))
								+ "后再来");
					}
				}
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
				Map<String, Object> ret = new Hashtable<String, Object>();
				ret.put("daystogive", yxaccount.getDaystogive());
				ret.put("timeexpire", yxaccount.getTimeexpire());
				ret.put("loginkey", yxlogin.getKey());
				ret.put("accountkey", yxaccount.getKey());
				Reuse.respond(response, ret, null);
			} else {
				yxaccount.setTimewrongpass(new Date());
				yxaccount.modify(yxaccount.getKey());
				throw new Exception("账号或密码或格言不正确，请重新登录");
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}