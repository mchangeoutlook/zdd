package com.tenotenm.yanxin.servlets;

import java.io.IOException;
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
				throw new Exception("缺少账号名称");
			}
			
			String pass = request.getParameter("pass");
			String repass = request.getParameter("repass");
			if (pass == null || pass.trim().isEmpty()||repass==null||repass.trim().isEmpty()) {
				throw new Exception("缺少密码");
			}
			
			String motto = request.getParameter("motto");
			String remotto = request.getParameter("remotto");
			if (motto == null || motto.trim().isEmpty()||motto==null||remotto.trim().isEmpty()) {
				throw new Exception("缺少格言");
			}
			
			Yxaccount yxaccount = new Yxaccount();
			try {
				yxaccount.setIp(Reuse.getremoteip(request));
				Bizutil.setpassandmotto(yxaccount, name, pass, repass, motto, remotto);
				Bizutil.settimecreate(yxaccount);
				yxaccount.setUa(Reuse.getuseragent(request));
				yxaccount.setYxloginkey("");
				yxaccount.setYxyanxinuniquekeyprefix(Bigclient.newbigdatakey());
				yxaccount.createunique(null, name);
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					yxaccount.readunique(name);
					if (Bizutil.iswaitingfirstlogin(yxaccount)||Bizutil.isbeforereusedate(yxaccount)) {
						throw new Exception("账号名称被占用，请换一个名称");
					}
					if (!yxaccount.getYxloginkey().isEmpty()) {
						Yxlogin yxlogin = new Yxlogin();
						yxlogin.read(yxaccount.getYxloginkey());
						Bizutil.logout(yxlogin);
					}
					Bizutil.settimecreate(yxaccount);
					Bizutil.setpassandmotto(yxaccount, null, pass, repass, motto, remotto);
					yxaccount.setIp(Reuse.getremoteip(request));
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