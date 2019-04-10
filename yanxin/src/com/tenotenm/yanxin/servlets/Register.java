package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.filters.Check;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/register")
public class Register extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Check.iplimit(Reuse.getremoteip(request), false);

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
			if (remotto==null||!motto.toLowerCase().trim().equals(remotto.toLowerCase().trim())) {
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
				Date timeexpire = new Date(yxaccount.getTimecreate().getTime() + (Long.parseLong(
						Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("freedays"))
						+ 1) * 24 * 60 * 60 * 1000);
				yxaccount.setTimeexpire(timeexpire);
				yxaccount.setUa(Reuse.getuseragent(request));
				yxaccount.setTimewrongpass(new Date(yxaccount.getTimecreate().getTime()-Long.parseLong(Configclient
						.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("wrongpasswaitseconds"))
						* 1000));
				yxaccount.setUniquename(name);
				yxaccount.setYxloginkey("");
				yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
				yxaccount.createunique(null, name);
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					yxaccount.readunique(name);
					if (System.currentTimeMillis()-yxaccount.getTimeexpire().getTime()<(Long.parseLong(
							Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("freedays"))
							+ 1) * 24 * 60 * 60 * 1000) {
						throw new Exception("账号名称被占用，请换一个名称");
					} else {
						yxaccount.setIp(Reuse.getremoteip(request));
						yxaccount.setMotto(Reuse.sign(motto));
						yxaccount.setPass(Reuse.sign(pass));
						Date timeexpire = new Date(yxaccount.getTimecreate().getTime() + (Long.parseLong(
								Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("freedays"))
								+ 1) * 24 * 60 * 60 * 1000);
						yxaccount.setTimeexpire(timeexpire);
						yxaccount.setUa(Reuse.getuseragent(request));
						yxaccount.setTimewrongpass(new Date(yxaccount.getTimecreate().getTime()-Long.parseLong(Configclient
								.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("wrongpasswaitseconds"))
								* 1000));
						yxaccount.setUniquename(name);
						yxaccount.setYxloginkey("");
						yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
						yxaccount.modify(yxaccount.getKey());
					}
				} else {
					throw e;
				}
			}
			Check.iplimit(Reuse.getremoteip(request), true);
			Reuse.respond(response, null, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}