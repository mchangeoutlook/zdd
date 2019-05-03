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
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/readself")
public class Readself extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			
			Map<String, String> ret = new Hashtable<String, String>();
			
			ret.put("daystogive", String.valueOf(yxaccount.getDaystogive()));
			ret.put("timecreate", Reuse.yyyyMMddHHmmss(yxaccount.getTimecreate()));
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
			ret.put("timereuse", Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(yxaccount)));
			ret.put("today", Reuse.yyyyMMdd(new Date()));
			ret.put("name", yxaccount.getName());
			if (Bizutil.isadmin(yxaccount)) {
				ret.put("isadmin", "t");
				ret.put("accountkey", yxaccount.getKey());
			} else {
				ret.put("isadmin", "f");
			}	
			if (yxaccount.getDaystogive()>0) {
				if (new Date().after(new Date(yxaccount.getTimeexpire().getTime()-Reuse.getlongvalueconfig("extend.expire.in.days")*24*60*60*1000))) {
					ret.put("accountkey", yxaccount.getKey());
				} else {
					ret.put("extendselfaftertime", Reuse.yyyyMMddHHmmss(new Date(yxaccount.getTimeexpire().getTime()-Reuse.getlongvalueconfig("extend.expire.in.days")*24*60*60*1000)));
				}
			}
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}