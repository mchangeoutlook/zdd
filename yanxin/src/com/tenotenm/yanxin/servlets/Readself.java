package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Dataclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;

@SuppressWarnings("serial")
@WebServlet("/creused/readself")
public class Readself extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			
			Map<String, Object> ret = new Hashtable<String, Object>();
			
			ret.put("daystogive", String.valueOf(yxaccount.getDaystogive()));
			ret.put("timecreate", Reuse.yyyyMMddHHmmss(yxaccount.getTimecreate()));
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
			ret.put("timereuse", Reuse.yyyyMMddHHmmss(Bizutil.datedenyreuseaccount(yxaccount)));
			ret.put("name", yxaccount.getName());
			boolean canextend = new Date().after(Bizutil.canextenddate(yxaccount));
			if (!canextend) {
				ret.put("extendselfaftertime", Reuse.yyyyMMddHHmmss(Bizutil.canextenddate(yxaccount)));
			}
			if (yxaccount.getDaystogive()>0&&canextend) {
				ret.put("accountkey", yxaccount.getKey());
			}
			
			if (Bizutil.isadmin(yxaccount)) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				long statdays = Reuse.getlongvalueconfig("read.stat.days");
				Vector<Map<String, String>> daystat = new Vector<Map<String, String>>();
				for (int i=0;i<statdays;i++) {
					c.add(Calendar.DATE, -1);
					String statkey = Uniqueindexclient.getinstance(Reuse.namespace_yanxin, "stat-" + Reuse.yyyyMMdd(c.getTime())).readunique(Reuse.filter_paged);
					if (statkey!=null&&!statkey.trim().isEmpty()) {
						Map<String, String> sr = Dataclient.getinstance(Reuse.namespace_yanxin, "stat").key(statkey).add("total").add("new").add("write").read(Reuse.app_data);
						Map<String, String> s = new Hashtable<String, String>();
						s.put("day", Reuse.yyyyMMdd(c.getTime()));
						if (sr.get("total")!=null) {
							s.put("total", sr.get("total"));
						} else{
							s.put("total", "0");
						}
						if (sr.get("new")!=null) {
							s.put("newcom", sr.get("new"));
						} else{
							s.put("newcom", "0");
						}
						if (sr.get("write")!=null) {
							s.put("write", sr.get("write"));
						} else{
							s.put("write", "0");
						}
						daystat.add(s);
					}
				}
				ret.put("daystat", daystat);
			} else {
				if (yxaccount.getDaystogive()>0) {
					ret.put("timecleardaystogive", Reuse.yyyyMMddHHmmss(Bizutil.cleardaystogive(yxaccount)));
				}
			}
			if (Bizutil.isaccountexpired(yxaccount)) {
				ret.put("isexpired", "t");
			} else {
				ret.put("isexpired", "f");
			}
			ret.put("today", Reuse.yyyyMMdd(new Date()));
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
}