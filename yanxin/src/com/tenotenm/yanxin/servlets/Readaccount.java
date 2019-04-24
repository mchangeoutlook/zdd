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
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/readaccount")
public class Readaccount extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			if (yxaccount.getDaystogive()<=0) {
				throw new Exception("无权查询账号");
			}
			
			Yxaccount target = new Yxaccount();
			try {
				target.readunique(request.getParameter("targetname"));
			}catch(Exception e) {
				throw new Exception("该账号不存在");
			}
			Map<String, String> ret = new Hashtable<String, String>();
			ret.put("key", target.getKey());
			ret.put("name", target.getUniquename());
			ret.put("daystogive", String.valueOf(target.getDaystogive()));
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(target.getTimeexpire()));
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}