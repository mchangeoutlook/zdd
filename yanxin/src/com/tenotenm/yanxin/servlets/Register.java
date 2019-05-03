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
			Bizutil.iplimit(Reuse.getremoteip(request), false);

			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) {
				throw new Exception("请填写账号");
			}
			
			String pass = request.getParameter("pass");
			String repass = request.getParameter("repass");
			if (pass == null || pass.trim().isEmpty()||repass==null||repass.trim().isEmpty()) {
				throw new Exception("请填写密码");
			}
			
			if (!pass.equals(repass)) {
				throw new Exception("两次填写的密码不一致");
			}
			
			String motto = request.getParameter("motto");
			String remotto = request.getParameter("remotto");
			if (motto == null || motto.trim().isEmpty()||motto==null||remotto.trim().isEmpty()) {
				throw new Exception("请填写格言");
			}
			
			if (!motto.toLowerCase().trim().equals(remotto.toLowerCase().trim())) {
				throw new Exception("两次填写的格言不一致");
			}
			
			Yxaccount yxaccount = new Yxaccount();
			try {
				yxaccount.setIp(Reuse.getremoteip(request));
				yxaccount.setName(name);
				yxaccount.setPass(pass);
				yxaccount.setMotto(motto);
				yxaccount.setTimecreate(new Date());
				yxaccount.setUa(Reuse.getuseragent(request));
				yxaccount.setYxloginkey("");
				yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
				yxaccount.createunique(null, yxaccount.getUniquename());
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					yxaccount.readunique(yxaccount.getUniquename());
					if (Bizutil.iswaitingfirstlogin(yxaccount)||Bizutil.isbeforereusedate(yxaccount)) {
						throw new Exception("账号被占用，请换一个名称");
					}
					if (!yxaccount.getYxloginkey().isEmpty()) {
						Yxlogin yxlogin = new Yxlogin();
						yxlogin.read(yxaccount.getYxloginkey());
						Bizutil.logout(yxlogin);
					}
					yxaccount.setIp(Reuse.getremoteip(request));
					yxaccount.setName(name);
					yxaccount.setPass(pass);
					yxaccount.setMotto(motto);
					yxaccount.setTimecreate(new Date());
					yxaccount.setUa(Reuse.getuseragent(request));
					yxaccount.setYxloginkey("");
					yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
					yxaccount.modify(yxaccount.getKey());
				} else {
					throw e;
				}
			}
			Bizutil.iplimit(Reuse.getremoteip(request), true);
			Reuse.respond(response,
					"注册成功！请在" + Reuse.yyyyMMddHHmmss(Bizutil.dateallowfirstlogin(yxaccount))
							+ "前完成首次登录，否则账号将被回收",
					null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	
	}
	
}