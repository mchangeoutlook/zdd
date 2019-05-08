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
@WebServlet("/check/extend")
public class Extendexpire extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			long toincrease = 0;
			try {
				toincrease = Long.parseLong(request.getParameter("incre"));
			}catch(Exception e) {
				//do nothing
			}
			if (toincrease<=0||toincrease>Reuse.getlongvalueconfig("extend.expire.days.max")) {
				throw new Exception("延长天数必须在1到"+Reuse.getlongvalueconfig("extend.expire.days.max")+"之间");
			}
			if (yxaccount.getDaystogive()<toincrease) {
				throw new Exception("你的库存天数不足");
			}
			Yxaccount target = new Yxaccount();
			target.setName(request.getParameter("targetname"));
			try {
				target.readunique(target.getUniquename());
			}catch(Exception e) {
				throw new Exception("无效目标账号");
			}
			
			Bizutil.checkaccountreused(target);
			
			if (new Date().before(new Date(target.getTimeexpire().getTime()-Reuse.getlongvalueconfig("extend.expire.in.days")*24*60*60*1000))) {
				throw new Exception("只能给"+Reuse.getlongvalueconfig("extend.expire.in.days")+"天内过期的账号延长过期时间");
			}
			
			yxaccount.setDaystogive4increment(-1*toincrease);
			yxaccount.increment(null);
			
			if (yxaccount.getDaystogive()<0) {
				yxaccount.setDaystogive4increment(toincrease);
				yxaccount.increment(null);
				throw new Exception("你的库存天数不够");
			}
			
			if (new Date().before(target.getTimeexpire())) {
				target.setTimeexpire(new Date(target.getTimeexpire().getTime()+toincrease*24*60*60*1000));
			} else {
				target.setTimeexpire(new Date(System.currentTimeMillis()+toincrease*24*60*60*1000));
			}
			target.setTimeupdate(new Date());
			target.modify(null);
			Map<String, String> ret = new Hashtable<String, String>();
			if (!target.getKey().equals(yxaccount.getKey())) {
				ret.put("selforother", "other");
				yxaccount.setTimeupdate(new Date());
				yxaccount.modify(null);
			} else {
				ret.put("selforother", "self");
			}
			ret.put("timeexpire", Reuse.yyyyMMddHHmmss(target.getTimeexpire()));
			ret.put("daystogive", String.valueOf(yxaccount.getDaystogive()));
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}