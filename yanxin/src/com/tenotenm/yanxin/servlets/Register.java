package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Iplimit;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/register")
public class Register extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			iplimit(Reuse.getremoteip(request), false);

			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) {
				throw new Exception("缺少账号名称");
			}
			name = name.toLowerCase().trim();
			if (name.getBytes("UTF-8").length > 60) {
				throw new Exception("账号名称过长");
			}

			String pass = request.getParameter("pass");
			String repass = request.getParameter("repass");
			if (pass == null || pass.trim().isEmpty()) {
				throw new Exception("缺少密码");
			}
			if (!pass.equals(repass)) {
				throw new Exception("两次输入的密码不一致");
			}
			if (Reuse.sign(pass).getBytes("UTF-8").length > 60) {
				throw new Exception("密码过长");
			}

			String motto = request.getParameter("motto");
			String remotto = request.getParameter("remotto");
			if (motto == null || motto.trim().isEmpty()) {
				throw new Exception("缺少格言");
			}
			if (remotto == null || !motto.toLowerCase().trim().equals(remotto.toLowerCase().trim())) {
				throw new Exception("两次输入的格言不一致");
			}
			motto = motto.toLowerCase().trim();
			if (Reuse.sign(motto).getBytes("UTF-8").length > 300) {
				throw new Exception("格言过长");
			}

			Yxaccount yxaccount = new Yxaccount();
			try {
				yxaccount.setIp(Reuse.getremoteip(request));
				yxaccount.setMotto(Reuse.sign(motto));
				yxaccount.setPass(Reuse.sign(pass));
				Date timeexpire = new Date(yxaccount.getTimecreate().getTime()
						+ Reuse.getdaysmillisconfig("freeuse.days"));
				yxaccount.setTimeexpire(timeexpire);
				yxaccount.setUa(Reuse.getuseragent(request));
				yxaccount.setTimewrongpass(new Date(yxaccount.getTimecreate().getTime()
						- Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")));
				yxaccount.setUniquename(name);
				yxaccount.setYxloginkey("");
				yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
				yxaccount.createunique(null, name);
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					yxaccount.readunique(name);
					if (Login.iswaitingfirstlogin(yxaccount)||Login.isbeforereusedate(yxaccount)) {
						throw new Exception("账号名称被占用，请换一个名称");
					}
					if (!yxaccount.getYxloginkey().isEmpty()) {
						Yxlogin yxlogin = new Yxlogin();
						yxlogin.read(yxaccount.getYxloginkey());
						Logout.logout(yxlogin);
					}
					Date now = new Date();
					yxaccount.setTimecreate(now);
					yxaccount.setTimeupdate(now);
					yxaccount.setIp(Reuse.getremoteip(request));
					yxaccount.setMotto(Reuse.sign(motto));
					yxaccount.setPass(Reuse.sign(pass));
					Date timeexpire = new Date(yxaccount.getTimecreate().getTime()
							+ Reuse.getdaysmillisconfig("freeuse.days"));
					yxaccount.setTimeexpire(timeexpire);
					yxaccount.setUa(Reuse.getuseragent(request));
					yxaccount.setTimewrongpass(new Date(yxaccount.getTimecreate().getTime()
							- Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")));
					yxaccount.setUniquename(name);
					yxaccount.setYxloginkey("");
					yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
					yxaccount.modify(yxaccount.getKey());

				} else {
					throw e;
				}
			}
			iplimit(Reuse.getremoteip(request), true);
			Reuse.respond(response,
					"请在" + Reuse.yyyyMMddHHmmss(Login.dateallowfirstlogin(yxaccount))
							+ "前完成首次登录，否则账号将被回收",
					null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	
	}

}