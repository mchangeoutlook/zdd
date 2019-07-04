package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/register")
public class Register extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String ip = Reuse.getremoteip(request);
			Bizutil.ipdeny(ip, false);
			Bizutil.iplimit(ip, false);

			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) {
				throw new Exception(Reuse.msg_hint+"请填写账号");
			}
			
			String pass = request.getParameter("pass");
			if (pass == null || pass.trim().isEmpty()) {
				throw new Exception(Reuse.msg_hint+"请提供密码");
			}
			
			Yxaccount yxaccount = new Yxaccount();
			try {
				yxaccount.setIp(Reuse.getremoteip(request));
				yxaccount.setName(name);
				yxaccount.setPass(pass);
				yxaccount.setTimecreate(new Date());
				yxaccount.setUa(Reuse.getuseragent(request));
				yxaccount.setYxloginkey("");
				yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
				yxaccount.createunique(null, yxaccount.getUniquename());
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					Yxaccount exist = new Yxaccount();
					exist.readunique(yxaccount.getUniquename());
					if (Bizutil.isbeforereusedate(exist)) {
						throw new Exception(Reuse.msg_hint+"账号被占用，请换一个名称");
					}
					if (!exist.getYxloginkey().isEmpty()) {
						Yxlogin yxlogin = new Yxlogin();
						yxlogin.read(exist.getYxloginkey());
						Bizutil.logout(yxlogin);
					}
					yxaccount.create(null);
					yxaccount.modifyunique(yxaccount.getKey(), yxaccount.getUniquename());
				} else {
					throw e;
				}
			}
			Bizutil.iplimit(Reuse.getremoteip(request), true);
			Reuse.respond(response,
					"注册成功！你的账号过期时间为："+Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()) + "，回收时间为："+Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(yxaccount))
							+ "，请仔细且完整地阅读你的心之言协议",
					null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	
	}
	
}