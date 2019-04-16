package com.tenotenm.yanxin.filters;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Ipdeny;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.util.STATIC;

@WebFilter("/check/*")
public class Check implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse res = (HttpServletResponse) arg1;
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Origin", "*");
		String ip = Reuse.getremoteip(req);
		try {
			ipdeny(ip, false);
			Yxlogin yxlogin = new Yxlogin();
			yxlogin.read(req.getParameter("loginkey"));

			if (!ip.equals(yxlogin.getIp()) || !Reuse.getuseragent(req).equals(yxlogin.getUa())) {
				throw new Exception("已启动IP保护，请重新登录");
			}

			if (yxlogin.getIslogout()) {
				throw new Exception("已退出，请重新登录");
			}
			if (System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Reuse.getsecondsmillisconfig("session.expire.seconds")) {
				throw new Exception("已过期，请重新登录");
			}

			Yxaccount yxaccount = new Yxaccount();
			yxaccount.read(yxlogin.getYxaccountkey());

			if (!yxaccount.getYxloginkey().equals(yxlogin.getKey())) {
				throw new Exception("非法访问，请重新登录");
			}
			
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());

			req.setAttribute(Yxaccount.class.getSimpleName(), yxaccount);
			req.setAttribute(Yxlogin.class.getSimpleName(), yxlogin);
			
			arg2.doFilter(req, res);
			
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains(STATIC.INVALIDKEY)) {
				try {
					ipdeny(ip, true);
					throw new Exception("无效访问，请重新登录");
				} catch (Exception e1) {
					Reuse.respond(res, null, e1);
				}
			} else {
				Reuse.respond(res, null, e);
			}
		}
	}

	public static void ipdeny(String ip, boolean todeny) throws Exception {
		Ipdeny ipd = new Ipdeny();
		Vector<String> ipdkeys = ipd.readpaged(ip);
		Date timeback = null;
		if (ipdkeys.isEmpty()) {
			if (todeny) {
				ipd.setIp(ip);
				ipd.createpaged(null, ip);
			}
		} else {
			ipd.read(ipdkeys.get(0));
			
			if (System.currentTimeMillis() - ipd.getTimedeny()
					.getTime() < Reuse.getsecondsmillisconfig("ipdeny.wait.seconds")) {
				timeback = ipd.getTimedeny();
			}
			if (timeback == null&&todeny) {
				ipd.setTimedeny(new Date());
				ipd.modify(ipd.getKey());
			}
			
		}
		if (timeback != null) {
			throw new Exception("已启动IP保护，请" + Reuse.yyyymmddhhmmss(new Date(timeback.getTime()
					+ Reuse.getsecondsmillisconfig("ipdeny.wait.seconds")))
					+ "后再来");
		}
	}

}
