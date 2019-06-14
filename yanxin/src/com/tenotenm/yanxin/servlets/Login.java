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

import com.tenotenm.yanxin.entities.Accountipdeny;
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
			String ip = Reuse.getremoteip(request);
			Bizutil.ipdeny(ip, false);
			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) {
				throw new Exception(Reuse.msg_hint+"请填写账号");
			}
			Yxaccount yxaccount = new Yxaccount();
			yxaccount.setName(name);
			try {
				yxaccount.readunique(yxaccount.getUniquename());
			} catch (Exception e) {
				throw new Exception(Reuse.msg_hint+"账号不存在，请重新登录");
			}

			Bizutil.autoupdateaccount(yxaccount, null);

			Bizutil.checkaccountreused(yxaccount);

			Accountipdeny aipdeny = new Accountipdeny();
			try {
				String aipdenykey = aipdeny.readpaged(yxaccount.getUniquename()+"-"+ip).get(0);
				aipdeny.read(aipdenykey);
			}catch(Exception e) {
				aipdeny=null;
			}
					
			if (aipdeny!=null&&ip.equals(aipdeny.getWrongpassip()) && System.currentTimeMillis()
					- aipdeny.getWrongpasstime().getTime() < Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")) {
				throw new Exception(
						Reuse.msg_hint+"已启动账号保护，请于" + Reuse.yyyyMMddHHmmss(new Date(aipdeny.getWrongpasstime().getTime()
								+ Reuse.getsecondsmillisconfig("wrongpass.wait.seconds"))) + "之后再来");
			}

			String pass = request.getParameter("pass");
			if (pass == null || pass.trim().isEmpty()) {
				throw new Exception(Reuse.msg_hint+"请提供密码");
			}
			if (pass != null && !pass.trim().isEmpty() && yxaccount.getPass().equals(Reuse.sign(pass))) {
				Yxlogin yxlogin = new Yxlogin();
				yxlogin.setIp(Reuse.getremoteip(request));
				yxlogin.setUa(Reuse.getuseragent(request));
				yxlogin.setYxaccountkey(yxaccount.getKey());
				yxlogin.create(null);
				if (yxaccount.getYxloginkey() != null && !yxaccount.getYxloginkey().isEmpty()) {
					Yxlogin prevlogin = new Yxlogin();
					prevlogin.read(yxaccount.getYxloginkey());
					Bizutil.logout(prevlogin);
				}
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
				boolean canextend = new Date().after(new Date(yxaccount.getTimeexpire().getTime()
						- Reuse.getlongvalueconfig("extend.expire.in.days") * 24 * 60 * 60 * 1000));
				if (canextend) {
					ret.put("expiretime", Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
				}
				ret.put("name", yxaccount.getName());
				ret.put("today", Reuse.yyyyMMdd(new Date()));
				if (aipdeny!=null) {
					aipdeny.setWrongpasstimes4increment(-1*aipdeny.getWrongpasstimes());
					aipdeny.increment(null);
				}
				Reuse.respond(response, ret, null);
			} else {
				if (aipdeny==null) {
					aipdeny = new Accountipdeny();
					aipdeny.setWrongpassip(ip);
					aipdeny.setWrongpasstime(new Date(System.currentTimeMillis()-Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")));
					aipdeny.createpaged(null, yxaccount.getUniquename()+"-"+ip,true);
				}
				aipdeny.setWrongpasstimes4increment(1l);
				aipdeny.increment(null);
				if (aipdeny.getWrongpasstimes() < Reuse.getlongvalueconfig("wrongpass.times.max")) {
					throw new Exception(Reuse.msg_hint+"密码不正确，你还可以尝试"
							+ (Reuse.getlongvalueconfig("wrongpass.times.max") - aipdeny.getWrongpasstimes())
							+ "次，如果依旧不能提供正确密码，则需等待" + Reuse.getsecondsmillisconfig("wrongpass.wait.seconds") / 60000
							+ "分钟才能重新登录");
				} else {
					aipdeny.setWrongpasstimes4increment(-1*aipdeny.getWrongpasstimes());
					aipdeny.increment(null);
					aipdeny.setWrongpassip(ip);
					aipdeny.setWrongpasstime(new Date());
					aipdeny.modify(null);
					throw new Exception(Reuse.msg_hint+"密码不正确，由于尝试次数过多，已启动账号保护，请于"
							+ Reuse.yyyyMMddHHmmss(new Date(aipdeny.getWrongpasstime().getTime() + Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")))
							+ "之后重新登录");
				}
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}