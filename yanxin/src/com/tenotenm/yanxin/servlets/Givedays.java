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
@WebServlet("/check/give")
public class Givedays extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Yxaccount target = new Yxaccount();
			target.setName(request.getParameter("targetname"));
			
			if (yxaccount.getUniquename().equals(target.getUniquename())) {
				throw new Exception("不能给自己增加天数");
			}
			
			long toincrease = 0;
			try {
				toincrease = Long.parseLong(request.getParameter("incre"));
			}catch(Exception e) {
				//do nothing
			}
			if (toincrease<=0||toincrease>Reuse.getlongvalueconfig("days.togive.max")) {
				throw new Exception("增加天数必须在1到"+Reuse.getlongvalueconfig("days.togive.max")+"之间");
			}
			if (yxaccount.getDaystogive()<toincrease) {
				throw new Exception("你的库存天数不足");
			}
			try {
				target.readunique(target.getUniquename());
			}catch(Exception e) {
				throw new Exception("目标账号无效");
			}
			
			Bizutil.checkaccountreused(target);
			
			Bizutil.checkaccountavailability(target);
			
			yxaccount.setDaystogive4increment(-1*toincrease);
			yxaccount.increment(null);
			
			if (yxaccount.getDaystogive()<0) {
				yxaccount.setDaystogive4increment(toincrease);
				yxaccount.increment(null);
				throw new Exception("你的库存天数不够");
			}
			
			target.setDaystogive4increment(toincrease);
			target.increment(null);
			
			target.setTimeupdate(new Date());
			target.modify(null);
			
			yxaccount.setTimeupdate(new Date());
			yxaccount.modify(null);
			Map<String, String> ret = new Hashtable<String, String>();
			ret.put("otherdaystogive", String.valueOf(target.getDaystogive()));
			ret.put("daystogive", String.valueOf(yxaccount.getDaystogive()));
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}