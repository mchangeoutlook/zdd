package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/readother")
public class Readother extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Yxaccount target = new Yxaccount();
			target.setName(request.getParameter("targetname"));
			if (!yxaccount.getUniquename().equals(target.getUniquename())) {
				Bizutil.checkaccountavailability(yxaccount);
				if (yxaccount.getDaystogive()<=0) {
					throw new Exception("无权查询账号");
				}
				try {
					target.readunique(target.getUniquename());
				}catch(Exception e) {
					throw new Exception("该账号不存在");
				}
			} else {
				throw new Exception("你的账号信息已显示，无需再查询");
			}
			
			Map<String, String> ret = new Hashtable<String, String>();
			
			ret.put("daystogive", String.valueOf(target.getDaystogive()));
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(target.getTimeexpire()));
			ret.put("timereuse", Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(target)));
			ret.put("name", target.getName());
			ret.put("otheraccountkey", target.getKey());
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}