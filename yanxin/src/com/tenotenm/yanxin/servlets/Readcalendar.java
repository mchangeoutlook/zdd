package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.entities.Yxyanxin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;

@SuppressWarnings("serial")
@WebServlet("/check/calendar")
public class Readcalendar extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			
			Bizutil.checkaccountavailability(yxaccount);
			
			String year = null;
			int yearint = 0;
			try{
				yearint = Integer.parseInt(request.getParameter("year"));
				if (yearint<2000||yearint>9999) {
					throw new Exception();
				}
				year = request.getParameter("year");
			}catch(Exception e) {
				year = Reuse.yyyyMMdd(new Date()).split("-")[0];
				yearint = Integer.parseInt(year);
			}
			
			String month = null;
			int monthint = 0;
			try{
				monthint = Integer.parseInt(request.getParameter("month"));
				if (monthint<1||monthint>12) {
					throw new Exception();
				}
				month = Reuse.yyyyMMdd(new SimpleDateFormat("MM").parse(request.getParameter("month"))).split("-")[1];
			}catch(Exception e) {
				month = Reuse.yyyyMMdd(new Date()).split("-")[1];
				monthint = Integer.parseInt(month);
			}
			
			Calendar c = Calendar.getInstance();
			c.set(yearint, monthint, 0);
			int daysOfMonth = c.get(Calendar.DAY_OF_MONTH);
			Vector<Vector<Map<String, String>>> onemonth = new Vector<Vector<Map<String, String>>>(6);
			for (int mw=0;mw<6;mw++) {
				Vector<Map<String, String>> oneweek = new Vector<Map<String, String>>(7);
				for (int wd=0;wd<7;wd++) {
					oneweek.add(new Hashtable<String, String>());
				}
				onemonth.add(oneweek);
			}
			int firstdayoffset=0;
			for (int i=0;i<daysOfMonth;i++) {
				c.set(yearint, monthint, i);
				int weekday = c.get(Calendar.DAY_OF_WEEK);
				if (i==0) {
					firstdayoffset=weekday;
				}
				Vector<Map<String, String>> theweek = onemonth.get((i+firstdayoffset-1)/7);
				theweek.get(weekday-1).put("monthday", String.valueOf(i+1));
				theweek.get(weekday-1).put("weekday", String.valueOf(weekday));
				try {
					Date theday = Reuse.yyyyMMddHHmmss(year+"-"+month+"-"+(i+1)+" 23:59:59");
					if (yxaccount.getTimecreate().before(theday)) {
						Yxyanxin yxyanxin = new Yxyanxin();
						yxyanxin.readunique(yxaccount.getYxyanxinuniquekeyprefix()+"-"+Reuse.yyyyMMdd(theday));
						theweek.get(weekday-1).putAll(Bizutil.readyanxin(yxlogin, yxaccount, theday));
					}
				}catch(Exception e) {
					//do nothing
				}
			}
			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("onemonth", onemonth);
			if (monthint>1&&yxaccount.getTimecreate().before(Reuse.yyyyMMdd(year+"-"+month+"-01"))) {
				ret.put("leftmonth", monthint-1);
			}
			ret.put("middlemonth", monthint);
			if (monthint<12&&new Date().after(Reuse.yyyyMMddHHmmss(year+"-"+month+"-"+daysOfMonth+" 23:59:59"))) {
				ret.put("rightmonth", monthint+1);
			}
			
			if (yxaccount.getTimecreate().before(Reuse.yyyyMMdd(year+"-01-01"))) {
				ret.put("leftyear", yearint-1);
			}
			ret.put("middleyear", year);
			if (new Date().after(Reuse.yyyyMMdd((yearint+1)+"-01-01"))) {
				ret.put("rightyear", yearint+1);
			}
			
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}
	
}