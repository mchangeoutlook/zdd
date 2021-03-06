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
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/cexpired/readother")
public class Readother extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin) request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Yxaccount target = new Yxaccount();
			target.setName(request.getParameter("targetname"));
			if (!yxaccount.getUniquename().equals(target.getUniquename())) {
				if (yxaccount.getDaystogive()<=0) {
					throw new Exception(Reuse.msg_hint+"无权查询账号");
				}
				try {
					target.readunique(target.getUniquename());
				}catch(Exception e) {
					throw new Exception(Reuse.msg_hint+"该账号不存在");
				}
				if (Bizutil.isadmin(target)) {
					throw new Exception(Reuse.msg_hint+"无权查询该账号");
				}
				Bizutil.checkaccountreused(target);
				Bizutil.cleardaystogive(target, yxlogin);
			} else {
				throw new Exception(Reuse.msg_hint+"你的账号信息已显示，无需再查询");
			}
			
			Map<String, String> ret = new Hashtable<String, String>();
			
			ret.put("daystogive", String.valueOf(target.getDaystogive()));
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(target.getTimeexpire()));
			ret.put("timecreate", Reuse.yyyyMMddHHmmss(target.getTimecreate()));
			ret.put("timereuse", Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(target)));
			ret.put("name", target.getUniquename());
			if (target.getDaystogive()>0) {
				ret.put("timecleardaystogive", Reuse.yyyyMMddHHmmss(Bizutil.cleardaystogive(target)));
			}
			if (Bizutil.isadmin(yxaccount)) {
				ret.put("otheraccountkey", target.getKey());
			}
			boolean canextend = new Date().after(Bizutil.canextenddate(target));
			if (!canextend) {
				ret.put("extendotheraftertime", Reuse.yyyyMMddHHmmss(Bizutil.canextenddate(target)));
			}
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}