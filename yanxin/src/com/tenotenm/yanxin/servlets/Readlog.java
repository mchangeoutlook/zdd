package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Actionlog;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/creused/readlog")
public class Readlog extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			
			long pagenum = 0;
			try{
				pagenum = Long.parseLong(request.getParameter("pagenum"));
			}catch(Exception e) {
				//do nothing
			}
			long onepageitems = Reuse.getlongvalueconfig("onepage.items");
			
			Actionlog alog = new Actionlog();
			Vector<String> alogkeys = alog.readpaged(yxaccount.getUniquename(), (yxaccount.getLognum()-1)/onepageitems-pagenum);
			
			Map<String, Object> ret = new Hashtable<String, Object>(2); 
			Vector<Map<String, String>> list = new Vector<Map<String, String>>(alogkeys.size());
			for (int i=alogkeys.size()-1;i>=0;i--) {
				alog.read(alogkeys.get(i));
				Map<String, String> r = new Hashtable<String, String>();
				r.put("time", Reuse.yyyyMMddHHmmss(alog.getTimecreate()));
				r.put("oldvalue", alog.getOldvalue());
				r.put("newvalue", alog.getNewvalue());
				r.put("action", alog.getAction());
				if (alog.getUniqueaccountnamefrom().equals(yxaccount.getUniquename())) {
					r.put("from", alog.getUniqueaccountnamefrom());
				}
				r.put("to", alog.getUniqueaccountnameto());
				list.add(r);
			}
			if (yxaccount.getLognum()==0) {
				ret.put("totalpages", 0);
			} else {
				ret.put("totalpages", (yxaccount.getLognum()-1)/onepageitems+1);
			}
			ret.put("list", list);
			ret.put("pagenum", pagenum);
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}