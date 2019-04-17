package com.tenotenm.yanxin.filters;

import java.io.IOException;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
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
			Bizutil.ipdeny(ip, false);
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
			
			if (Reuse.yyyyMMdd(new Date()).equals(Reuse.yyyyMMdd(yxlogin.getTimeupdate()))) {
				yxlogin.setTimeupdate(new Date());
				yxlogin.modify(yxlogin.getKey());
			} else {
				throw new Exception("迎接新的一天，请重新登录");
			}

			Bizutil.refreshaccount(yxaccount, null, null, null, null, null, null, null, null);
			
			Bizutil.checkaccountreused(yxaccount);
						
			req.setAttribute(Yxaccount.class.getSimpleName(), yxaccount);
			req.setAttribute(Yxlogin.class.getSimpleName(), yxlogin);
			
			arg2.doFilter(req, res);
			
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains(STATIC.INVALIDKEY)) {
				try {
					Bizutil.ipdeny(ip, true);
					throw new Exception("无效访问，请重新登录");
				} catch (Exception e1) {
					Reuse.respond(res, null, e1);
				}
			} else {
				Reuse.respond(res, null, e);
			}
		}
	}


}
