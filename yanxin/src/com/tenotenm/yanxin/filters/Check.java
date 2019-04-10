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

import com.tenotenm.yanxin.entities.Iplimit;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Configclient;
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
			if (System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Long.parseLong(Configclient
					.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("sessionexpireseconds"))
					* 1000) {
				throw new Exception("已过期，请重新登录");
			}

			Yxaccount yxaccount = new Yxaccount();
			yxaccount.read(yxlogin.getYxaccountkey());

			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());

			req.setAttribute(Yxaccount.class.getSimpleName(), yxaccount);
			req.setAttribute(Yxlogin.class.getSimpleName(), yxlogin);

		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains(STATIC.INVALIDKEY)) {
				try {
					ipdeny(ip, true);
				} catch (Exception e1) {
					Reuse.respond(res, null, e1);
				}
			} else {
				Reuse.respond(res, null, e);
			}
		}
	}

	public static void ipdeny(String ip, boolean todeny) throws Exception {
		Iplimit ipd = new Iplimit();
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
					.getTime() < Long.parseLong(Configclient
							.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("ipdenywaitseconds"))
							* 1000) {
				timeback = ipd.getTimedeny();
			}
			if (timeback == null&&todeny) {
				ipd.setTimedeny(new Date());
				ipd.modify(ipd.getKey());
			}
			
		}
		if (timeback != null) {
			throw new Exception("已启动IP保护，请" + Reuse.yyyymmddhhmmss(new Date(timeback.getTime()
					+ Long.parseLong(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE)
							.read("ipdenywaitseconds")) * 1000))
					+ "后再来");
		}
	}

	public static void iplimit(String ip, boolean toincrementnewaccountstoday) throws Exception {
		Iplimit ipd = new Iplimit();
		Vector<String> ipdkeys = ipd.readpaged(ip);
		boolean islimited = false;
		if (ipdkeys.isEmpty()) {
			if (toincrementnewaccountstoday) {
				ipd.setIp(ip);
				ipd.createpaged(null, ip);
				ipd.setNewaccounts4increment(1l);
				ipd.increment(ipd.getKey());
			}
		} else {
			ipd.read(ipdkeys.get(0));
			if (ipd.getNewaccounts() > Long.parseLong(Configclient
					.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("iplimitnewaccounts"))) {
				islimited = true;
			}
			if (!islimited&&toincrementnewaccountstoday) {
				ipd.setNewaccounts4increment(1l);
				ipd.increment(ipd.getKey());
			}
		}

		if (islimited) {
			throw new Exception("已启动账号保护，请明天再来");
		}
	}
}
