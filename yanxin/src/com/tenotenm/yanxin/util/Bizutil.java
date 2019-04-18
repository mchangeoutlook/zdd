package com.tenotenm.yanxin.util;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.tenotenm.yanxin.entities.Ipdeny;
import com.tenotenm.yanxin.entities.Iplimit;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.entities.Yxyanxin;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

public class Bizutil {
	public static Integer newdaycomingminutes(Yxlogin yxlogin) throws Exception {
		long millistotomorrow = 24 * 60 * 60 * 1000 - (yxlogin.getTimeupdate().getTime()
				- Reuse.yyyyMMdd(Reuse.yyyyMMdd(yxlogin.getTimeupdate())).getTime());
		if (millistotomorrow < 60 * 60000) {
			return (int) (millistotomorrow / 60000);
		} else {
			return null;
		}
	}

	public static void ipdeny(String ip, boolean todeny) throws Exception {
		Ipdeny ipd = new Ipdeny();
		Vector<String> ipdkeys = ipd.readpaged(ip);
		Date timeback = null;
		if (ipdkeys.isEmpty()) {
			if (todeny) {
				ipd.setIp(ip);
				ipd.createpaged(null, ip);
			}
		} else {
			ipd.read(ipdkeys.get(0));

			if (System.currentTimeMillis() - ipd.getTimedeny().getTime() < Reuse
					.getsecondsmillisconfig("ipdeny.wait.seconds")) {
				timeback = ipd.getTimedeny();
			}
			if (timeback == null && todeny) {
				ipd.setTimedeny(new Date());
				ipd.modify(ipd.getKey());
			}

		}
		if (timeback != null) {
			throw new Exception("已启动IP保护，请"
					+ Reuse.yyyyMMddHHmmss(
							new Date(timeback.getTime() + Reuse.getsecondsmillisconfig("ipdeny.wait.seconds")))
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
			if (ipd.getNewaccounts() != null
					&& ipd.getNewaccounts() >= Reuse.getlongconfig("everyday.everyip.newaccounts.max")) {
				islimited = true;
			}
			if (!islimited && toincrementnewaccountstoday) {
				ipd.setNewaccounts4increment(1l);
				ipd.increment(ipd.getKey());
			}
		}

		if (islimited) {
			throw new Exception("已启动账号保护，请明天再来");
		}
	}

	public static Map<String, String> readyanxin(Yxlogin yxlogin, Yxaccount yxaccount, Date day) throws Exception {
		boolean istoday = Reuse.yyyyMMdd(day).equals(Reuse.yyyyMMdd(new Date()));
		Map<String, String> ret = new Hashtable<String, String>();
		if (istoday) {
			ret.put("istoday", "t");
		} else {
			ret.put("istoday", "f");
		}
		Yxyanxin yxyanxin = new Yxyanxin();
		try {
			yxyanxin.readunique(yxaccount.getYxyanxinuniquekeyprefix() + "-" + Reuse.yyyyMMdd(day));
		} catch (Exception e) {
			if (istoday) {
				yxyanxin.setContent("");
				yxyanxin.setLocation("");
				yxyanxin.setPhoto("");
				yxyanxin.setWeather("");
				yxyanxin.setYxloginkey(yxlogin.getKey());
				yxyanxin.createunique(null, yxaccount.getYxyanxinuniquekeyprefix() + "-" + Reuse.yyyyMMdd(day));
			} else {
				ret.put("key", "");
				return ret;
			}
		}
		ret.put("content", yxyanxin.getContent());
		ret.put("key", yxyanxin.getKey());
		ret.put("location", yxyanxin.getLocation());
		ret.put("photo", yxyanxin.getPhoto());
		ret.put("weather", yxyanxin.getWeather());
		ret.put("timecreate", Reuse.yyyyMMdd(yxyanxin.getTimecreate()));
		return ret;
	}

	public static void logout(Yxlogin yxlogin) throws Exception {
		if (!yxlogin.getIslogout()) {
			yxlogin.setIslogout(true);
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());
		}
	}

	public static boolean isadmin(Yxaccount yxaccount) {
		return yxaccount.getKey() != null && !yxaccount.getKey().trim().isEmpty()
				&& Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("admins")
						.contains(yxaccount.getKey());
	}

	public static Yxaccount refreshaccount(Yxaccount yxaccount, String loginkey, Date timecreate, String ip, String ua,
			String name, String pass, String motto, String yxyanxinuniquekeyprefix) throws Exception {
		if (timecreate != null) {
			yxaccount.setTimecreate(timecreate);
			yxaccount.setTimeupdate(timecreate);
			Date timeexpire = new Date(yxaccount.getTimecreate().getTime() + Reuse.getdaysmillisconfig("freeuse.days"));
			yxaccount.setTimeexpire(timeexpire);
			yxaccount.setTimewrongpass(new Date(
					yxaccount.getTimecreate().getTime() - Reuse.getsecondsmillisconfig("wrongpass.wait.seconds")));
		}
		if (ip != null) {
			yxaccount.setIp(ip);
		}
		if (motto != null) {
			yxaccount.setMotto(Reuse.sign(motto));
		}
		if (pass != null) {
			yxaccount.setPass(Reuse.sign(pass));
		}
		if (ua != null) {
			yxaccount.setUa(ua);
		}
		if (yxyanxinuniquekeyprefix != null) {
			yxaccount.setYxyanxinuniquekeyprefix(yxyanxinuniquekeyprefix);
		}
		if (name != null) {
			yxaccount.setUniquename(name);
		}
		if (loginkey != null) {
			yxaccount.setYxloginkey(loginkey);
		}
		if (isadmin(yxaccount)) {
			if (yxaccount.getDaystogive() < Reuse.getlongconfig("days.togive.max")) {
				yxaccount.setDaystogive4increment(Reuse.getlongconfig("days.togive.max") - yxaccount.getDaystogive());
				yxaccount.increment(yxaccount.getKey());
			}
			if (yxaccount.getTimeexpire().before(new Date())) {
				Date timeexpire = new Date(new Date().getTime() + Reuse.getdaysmillisconfig("freeuse.days"));
				yxaccount.setTimeexpire(timeexpire);
				yxaccount.modify(yxaccount.getKey());
			}
		}
		return yxaccount;
	}

	public static void checkaccountreused(Yxaccount yxaccount) throws Exception {
		if (isfirstlogindenied(yxaccount)) {
			throw new Exception("未及时完成首次登录，账号已被回收");
		}
		if (isreusing(yxaccount)) {
			throw new Exception("未及时延长账号有效期，账号已被回收");
		}
	}

	public static void checkaccountavailability(Yxaccount yxaccount) throws Exception {
		if (!isadmin(yxaccount)&&isaccountexpired(yxaccount)) {
			throw new Exception("账号已过期，请在"+Reuse.yyyyMMddHHmmss(datedenyreuseaccount(yxaccount))+"之前延长有效期，否则账号将被回收，回收后该账号的所有日记都将无法找回。");
		}
	}

	public static boolean isaccountexpired(Yxaccount yxaccount) throws Exception {
		Date now = new Date();
		return yxaccount.getTimeexpire().before(now)&&now.before(datedenyreuseaccount(yxaccount));
	}

	private static boolean isfirstlogindenied(Yxaccount yxaccount) {
		return yxaccount.getYxloginkey().isEmpty() & dateallowfirstlogin(yxaccount).before(new Date());
	}

	public static boolean iswaitingfirstlogin(Yxaccount yxaccount) {
		return yxaccount.getYxloginkey().isEmpty() & datedenyfirstlogin(yxaccount).after(new Date());
	}

	private static boolean isreusing(Yxaccount yxaccount) {
		return !yxaccount.getYxloginkey().isEmpty() & datedenyreuseaccount(yxaccount).before(new Date());
	}

	public static boolean isbeforereusedate(Yxaccount yxaccount) {
		return !yxaccount.getYxloginkey().isEmpty() & dateallowreuseaccount(yxaccount).after(new Date());
	}

	public static Date dateallowfirstlogin(Yxaccount yxaccount) {
		return new Date(datedenyfirstlogin(yxaccount).getTime() - 60000);
	}

	private static Date datedenyfirstlogin(Yxaccount yxaccount) {
		return new Date(yxaccount.getTimecreate().getTime() + Reuse.getsecondsmillisconfig("first.login.in.seconds"));
	}

	public static Date datedenyreuseaccount(Yxaccount yxaccount) {
		return new Date(dateallowreuseaccount(yxaccount).getTime() - 60000);
	}

	private static Date dateallowreuseaccount(Yxaccount yxaccount) {
		return new Date(yxaccount.getTimeexpire().getTime() + Reuse.getdaysmillisconfig("account.reuse.in.days"));
	}
}
