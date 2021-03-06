package com.tenotenm.yanxin.util;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Actionlog;
import com.tenotenm.yanxin.entities.Ipdeny;
import com.tenotenm.yanxin.entities.Iplimit;
import com.tenotenm.yanxin.entities.Onetimekey;
import com.tenotenm.yanxin.entities.Yanxin;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Fileclient;
import com.zdd.bdc.client.util.STATIC;

public class Bizutil {

	public static void log(Yxlogin yxlogin, Yxaccount from, Yxaccount to, String action, String oldvalue,
			String newvalue) throws Exception {
		Actionlog alog = new Actionlog();
		alog.setKey(Bigclient.newbigdatakey());
		alog.setNewvalue(newvalue);
		alog.setOldvalue(oldvalue);
		if (from == null) {
			alog.setUniqueaccountnamefrom("");
		} else {
			alog.setUniqueaccountnamefrom(from.getUniquename());
		}
		alog.setUniqueaccountnameto(to.getUniquename());
		alog.setYxloginkey(yxlogin.getKey());
		alog.setAction(action);
		to.setLognum4increment(1l);
		to.increment(null);
		alog.createpaged(to.getLognum() - 1, alog.getKey(), "actl-"+to.getKey(), true);
		if (from != null && !from.getUniquename().equals(to.getUniquename())) {
			from.setLognum4increment(1l);
			from.increment(null);
			alog.createpaged(from.getLognum() - 1, alog.getKey(), "actl-"+from.getKey(), false);
		}
	}

	public static void extendexpire(Yxaccount yxaccount, long toextenddays) throws Exception {
		yxaccount.setLocktoextendexpire4increment(1l);
		yxaccount.increment(null);
		try {
			if (yxaccount.getLocktoextendexpire() == 1) {
				if (new Date().before(yxaccount.getTimeexpire())) {
					yxaccount.setTimeexpire(
							new Date(yxaccount.getTimeexpire().getTime() + toextenddays * 24 * 60 * 60 * 1000));
				} else {
					yxaccount.setTimeexpire(new Date(System.currentTimeMillis() + toextenddays * 24 * 60 * 60 * 1000));
				}
				yxaccount.setTimeupdate(new Date());
				yxaccount.modify(null);
			} else {
				throw new Exception(Reuse.msg_hint + "过期时间变更冲突，请稍后再试");
			}
		} finally {
			yxaccount.setLocktoextendexpire4increment(-1l);
			yxaccount.increment(null);
		}
	}

	public static void givedays(Yxaccount yxaccount, long toincrease) throws Exception {
		yxaccount.setLocktoincreasedaystogive4increment(1l);
		yxaccount.increment(null);
		try {
			if (yxaccount.getLocktoincreasedaystogive() == 1) {
				yxaccount.setDaystogive4increment(toincrease);
				yxaccount.increment(null);
				yxaccount.setTimeupdate(new Date());
				yxaccount.setTimeupdatedaystogive(yxaccount.getTimeupdate());
				yxaccount.modify(null);
			} else {
				throw new Exception(Reuse.msg_hint + "库存天数变更冲突，请稍后再试");
			}
		} finally {
			yxaccount.setLocktoincreasedaystogive4increment(-1l);
			yxaccount.increment(null);
		}
	}

	public static String createyanxin(Yxaccount yxaccount, Yxlogin yxlogin, String photofolder, String photokey,
			String photosmallkey, Date today) throws Exception {
		Yanxin yx = Bizutil.readyanxin(yxaccount, today);
		if (yx == null) {
			yx = new Yanxin();
			yx.setKey(Bigclient.newbigdatakey());
			yx.setPhoto(photofolder + "/" + photokey);
			yx.setPhotosmall(photofolder + "/" + photosmallkey);
			yx.setContent("");
			yx.setLocation("");
			yx.setWeather("");
			yx.setEmotion("");
			yx.setUniquekeyprefix(yxaccount.getYxyanxinuniquekeyprefix());
			yx.setYxloginkey(yxlogin.getKey());
			yx.setTimecreate(new Date());
			try {
				yx.createunique(null, Bizutil.yanxinkey(yxaccount, today));
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains(STATIC.DUPLICATE)) {
					yx.readunique(Bizutil.yanxinkey(yxaccount, today));
					if (yx.getPhoto() != null && !yx.getPhoto().isEmpty()) {
						String[] folderkey = yx.getPhoto().split("/");
						if (folderkey.length == 2) {
							Fileclient.getinstance(folderkey[0]).delete(Reuse.namespace_yanxin, Reuse.app_file, folderkey[1]);
						}
					}
					if (yx.getPhotosmall() != null && !yx.getPhotosmall().isEmpty()
							&& !yx.getPhotosmall().equals(yx.getPhoto())) {
						String[] folderkey = yx.getPhotosmall().split("/");
						if (folderkey.length == 2) {
							Fileclient.getinstance(folderkey[0]).delete(Reuse.namespace_yanxin, Reuse.app_file, folderkey[1]);
						}
					}
					yx.setPhoto(photofolder + "/" + photokey);
					yx.setPhotosmall(photofolder + "/" + photosmallkey);
					yx.setYxloginkey(yxlogin.getKey());
					yx.modify(null);
				} else {
					throw e;
				}
			}
		} else {
			if (yx.getPhoto() != null && !yx.getPhoto().isEmpty()) {
				String[] folderkey = yx.getPhoto().split("/");
				if (folderkey.length == 2) {
					Fileclient.getinstance(folderkey[0]).delete(Reuse.namespace_yanxin, Reuse.app_file, folderkey[1]);
				}
			}
			if (yx.getPhotosmall() != null && !yx.getPhotosmall().isEmpty()
					&& !yx.getPhotosmall().equals(yx.getPhoto())) {
				String[] folderkey = yx.getPhotosmall().split("/");
				if (folderkey.length == 2) {
					Fileclient.getinstance(folderkey[0]).delete(Reuse.namespace_yanxin, Reuse.app_file, folderkey[1]);
				}
			}
			yx.setPhoto(photofolder + "/" + photokey);
			yx.setPhotosmall(photofolder + "/" + photosmallkey);
			yx.setYxloginkey(yxlogin.getKey());
			yx.modify(null);
		}
		return yx.getKey();
	}

	public static String newdaycominghint(Yxlogin yxlogin) throws Exception {
		long millistotomorrow = 24 * 60 * 60 * 1000 - (yxlogin.getTimeupdate().getTime()
				- Reuse.yyyyMMdd(Reuse.yyyyMMdd(yxlogin.getTimeupdate())).getTime());
		if (millistotomorrow < 60 * 60000) {
			int in = (int) (millistotomorrow / 60000);
			String instr = "";
			if (in==0) {
				instr = "不到 1 ";
			} else {
				instr = in+" ";
			}
			return "健康的身体需要充足的睡眠，请尽快结束今天的日记，" + instr
					+ "分钟后你将不能继续修改今天的日记，零点后你需要重新登录并开启第二天的日记";
		} else {
			return null;
		}
	}

	public static void logout(Yxlogin yxlogin) throws Exception {
		if (!yxlogin.getIslogout()) {
			yxlogin.setIslogout(true);
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());
		}
	}

	public static void ipdeny(String ip, boolean todeny) throws Exception {
		if (Float.parseFloat(String.valueOf(Runtime.getRuntime().totalMemory()))/Float.parseFloat(String.valueOf(Runtime.getRuntime().maxMemory()))>0.8) {
			throw new Exception(Reuse.msg_hint + "系统繁忙，请稍后再来");
		}
		Ipdeny ipd = new Ipdeny();
		String ipdkey = ipd.readpaged("ipd-"+ip);
		Date timeback = null;
		if (ipdkey==null||ipdkey.trim().isEmpty()) {
			if (todeny) {
				ipd.setIp(ip);
				ipd.createpaged(null, "ipd-"+ip, true);
			}
		} else {
			ipd.read(ipdkey);

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
			throw new Exception(Reuse.msg_hint + "已启动IP保护，请于"
					+ Reuse.yyyyMMddHHmmss(
							new Date(timeback.getTime() + Reuse.getsecondsmillisconfig("ipdeny.wait.seconds")))
					+ "之后重新登录");
		}
	}

	public static void iplimit(String ip, boolean toincrementnewaccountstoday) throws Exception {
		if (Reuse.getlongvalueconfig("everyday.everyip.newaccounts.max") < 0) {
			// no limit register on ip
		} else if (Reuse.getlongvalueconfig("everyday.everyip.newaccounts.max") == 0) {
			throw new Exception(Reuse.msg_hint + "暂不开放注册");
		} else {
			Iplimit ipl = new Iplimit();
			String iplkey = ipl.readpaged("ipl-"+ip);
			boolean islimited = false;
			if (iplkey==null||iplkey.trim().isEmpty()) {
				if (toincrementnewaccountstoday) {
					ipl.setIp(ip);
					ipl.createpaged(null, "ipl-"+ip, true);
					ipl.setNewaccounts4increment(1l);
					ipl.increment(null);
				}
			} else {
				ipl.read(iplkey);
				if (ipl.getNewaccounts() != null
						&& ipl.getNewaccounts() >= Reuse.getlongvalueconfig("everyday.everyip.newaccounts.max")) {
					islimited = true;
				}
				if (!islimited && toincrementnewaccountstoday) {
					ipl.setNewaccounts4increment(1l);
					ipl.increment(null);
				}
			}

			if (islimited) {
				throw new Exception(Reuse.msg_hint + "已启动账号保护，请明天再来");
			}
		}
	}

	public static Yanxin readyanxin(Yxaccount yxaccount, String key) throws Exception {
		Yanxin yxyanxin = new Yanxin();
		yxyanxin.read(key);
		if (!yxyanxin.getUniquekeyprefix().equals(yxaccount.getYxyanxinuniquekeyprefix())) {
			throw new Exception(Reuse.msg_hint + "无权查看别人的日记");
		}
		return yxyanxin;
	}

	public static Map<String, String> convert(Yanxin yanxin) {
		Map<String, String> ret = new Hashtable<String, String>();
		if (yanxin == null) {
			return ret;
		}
		if (yanxin.getContent() != null) {
			ret.put("content", yanxin.getContent());
		} else {
			ret.put("content", "");
		}
		ret.put("key", yanxin.getKey());
		if (yanxin.getLocation() != null) {
			ret.put("location", yanxin.getLocation());
		} else {
			ret.put("location", "");
		}
		if (yanxin.getPhoto() != null) {
			ret.put("photo", yanxin.getPhoto());
		} else {
			ret.put("photo", "");
		}
		if (yanxin.getWeather() != null) {
			ret.put("weather", yanxin.getWeather());
		} else {
			ret.put("weather", "");
		}
		if (yanxin.getEmotion() != null) {
			ret.put("emotion", yanxin.getEmotion());
		} else {
			ret.put("emotion", "");
		}
		if (yanxin.getTimecreate() != null) {
			ret.put("day", Reuse.yyyyMMdd(yanxin.getTimecreate()));
		} else {
			ret.put("day", "");
		}
		return ret;
	}

	public static String yanxinkey(Yxaccount yxaccount, Date day) {
		return yxaccount.getYxyanxinuniquekeyprefix() + "-" + Reuse.yyyyMMdd(day);
	}

	public static String onetimekey(String onetimekey, String yanxinkey) throws Exception {
		if (onetimekey == null) {
			onetimekey = Bigclient.newbigdatakey();
			Onetimekey ok = new Onetimekey();
			ok.createpaged(yanxinkey, onetimekey, false);
			return onetimekey;
		} else {
			Onetimekey ok = new Onetimekey();
			String existyanxinkey = ok.readpaged(onetimekey);
			if (existyanxinkey != null && !existyanxinkey.trim().isEmpty()) {
				ok.modifyunique(Bigclient.newbigdatakey(), onetimekey+"-0");
				return existyanxinkey;
			} else {
				return null;
			}
		}
	}

	public static Yanxin readyanxin(Yxaccount yxaccount, Date day) throws Exception {
		Yanxin yxyanxin = new Yanxin();
		try {
			yxyanxin.readunique(yanxinkey(yxaccount, day));
		} catch (Exception e) {
			return null;
		}
		return yxyanxin;
	}

	public static boolean isadmin(Yxaccount yxaccount) {
		return yxaccount.getKey() != null && !yxaccount.getKey().trim().isEmpty()
				&& Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_CORE).read("admins")
						.contains(yxaccount.getKey());
	}

	public static void cleardaystogive(Yxaccount yxaccount, Yxlogin yxlogin) throws Exception {
		if (yxaccount.getDaystogive() > 0) {
			Date cleardaystogive = cleardaystogive(yxaccount);
			if (cleardaystogive.before(new Date())) {
				String oldvalue = String.valueOf(yxaccount.getDaystogive());
				Bizutil.givedays(yxaccount, -1 * yxaccount.getDaystogive());
				Bizutil.log(yxlogin, null, yxaccount, "g", oldvalue, String.valueOf(yxaccount.getDaystogive()));
			}
		}
	}
	
	public static Date canextenddate(Yxaccount yxaccount) {
		return new Date(yxaccount.getTimeexpire().getTime()-Reuse.getlongvalueconfig("extend.expire.in.days")*24*60*60*1000);
	}
	
	public static void autoupdateaccount(Yxaccount yxaccount, Yxlogin yxlogin) throws Exception {
		if (isadmin(yxaccount)) {
			if (yxaccount.getDaystogive() < Reuse.getlongvalueconfig("days.togive.max")) {
				yxaccount.setDaystogive4increment(
						Reuse.getlongvalueconfig("days.togive.max") - yxaccount.getDaystogive());
				yxaccount.increment(yxaccount.getKey());
			}
			if (canextenddate(yxaccount).before(new Date())) {
				Date timeexpire = new Date(yxaccount.getTimeexpire().getTime() + Reuse.getdaysmillisconfig("freeuse.days"));
				yxaccount.setTimeexpire(timeexpire);
				yxaccount.setTimeupdate(new Date());
				yxaccount.modify(yxaccount.getKey());
			}
		} else {
			if (yxlogin != null) {
				cleardaystogive(yxaccount, yxlogin);
				if (!Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_CORE)
						.read("extend.all.expire.times.days").startsWith(yxaccount.getExtendallexpiremark()+".")) {
					long toextenddays = 0;
					String extendedmark = "";
					try {
						String[] mark_days = Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_CORE)
								.read("extend.all.expire.times.days").split("\\.");
						toextenddays = Long.parseLong(mark_days[1]);
						extendedmark = mark_days[0];
					}catch(Exception e) {
						//do nothing
					}
					if (toextenddays>0) {
						String oldvalue = Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire());
						Bizutil.extendexpire(yxaccount, toextenddays);
						Bizutil.log(yxlogin, null, yxaccount, "e", oldvalue, Reuse.yyyyMMddHHmmss(yxaccount.getTimeexpire()));
						yxaccount.setExtendallexpiremark(extendedmark);
						yxaccount.modify(null);
					}
				}
			}
		}
	}
	
	public static Date cleardaystogive(Yxaccount yxaccount) {
		return new Date(yxaccount.getTimeupdatedaystogive().getTime()
				+ Reuse.getdaysmillisconfig("account.reuse.in.days") - 60000);
	}

	public static void checkaccountreused(Yxaccount yxaccount) throws Exception {
		if (isreusing(yxaccount)) {
			throw new Exception(Reuse.msg_hint + "账号 " + yxaccount.getName() + " 已逾回收时间，已被回收");
		}
	}

	public static void checkaccountexpired(Yxaccount yxaccount) throws Exception {
		if (!isadmin(yxaccount) && isaccountexpired(yxaccount)) {
			throw new Exception(Reuse.msg_hint + "账号 " + yxaccount.getName() + " 已过期，请于"
					+ Reuse.yyyyMMddHHmmss(datedenyreuseaccount(yxaccount))
					+ "之前延长过期时间，否则该账号将被回收，回收后该账号无法登录且所有相关数据无法找回");
		}
	}

	public static boolean isaccountexpired(Yxaccount yxaccount) throws Exception {
		Date now = new Date();
		return yxaccount.getTimeexpire().before(now) && now.before(datedenyreuseaccount(yxaccount));
	}

	private static boolean isreusing(Yxaccount yxaccount) {
		return datedenyreuseaccount(yxaccount).before(new Date());
	}

	public static boolean isbeforereusedate(Yxaccount yxaccount) {
		return dateallowreuseaccount(yxaccount).after(new Date());
	}

	public static Date datedenyreuseaccount(Yxaccount yxaccount) {
		return new Date(dateallowreuseaccount(yxaccount).getTime() - 60000);
	}

	private static Date dateallowreuseaccount(Yxaccount yxaccount) {
		return new Date(yxaccount.getTimeexpire().getTime() + Reuse.getdaysmillisconfig("account.reuse.in.days"));
	}

	public static void commoncheck(HttpServletRequest req) throws Exception {
		String ip = Reuse.getremoteip(req);
		Bizutil.ipdeny(ip, false);
		Yxlogin yxlogin = new Yxlogin();
		yxlogin.read(req.getParameter("loginkey"));

		if (!ip.equals(yxlogin.getIp()) || !Reuse.getuseragent(req).equals(yxlogin.getUa())) {
			throw new Exception(Reuse.msg_hint + "已启动IP保护，请重新登录");
		}

		if (yxlogin.getIslogout()) {
			throw new Exception(Reuse.msg_hint + "已退出，请重新登录");
		}
		if (System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Reuse
				.getsecondsmillisconfig("session.expire.seconds")) {
			throw new Exception(Reuse.msg_hint + "你离开太久了，为了你的账号安全，请重新登录");
		}

		Yxaccount yxaccount = new Yxaccount();
		yxaccount.read(yxlogin.getYxaccountkey());

		if (!yxaccount.getYxloginkey().equals(yxlogin.getKey())) {
			throw new Exception(Reuse.msg_hint + "非法访问，请重新登录");
		}

		if (Reuse.yyyyMMdd(new Date()).equals(Reuse.yyyyMMdd(yxlogin.getTimeupdate()))) {
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());
		} else {
			throw new Exception(Reuse.msg_hint + "迎接新的一天，请重新登录");
		}

		Bizutil.autoupdateaccount(yxaccount, yxlogin);

		Bizutil.checkaccountreused(yxaccount);

		req.setAttribute(Yxaccount.class.getSimpleName(), yxaccount);
		req.setAttribute(Yxlogin.class.getSimpleName(), yxlogin);
	}

	public static void commoncheckexception(HttpServletRequest req, HttpServletResponse res, Exception e)
			throws IOException {
		if (e.getMessage() != null && e.getMessage().contains(Reuse.NOTFOUND)) {
			try {
				Bizutil.ipdeny(Reuse.getremoteip(req), true);
				throw new Exception(Reuse.msg_hint + "无效访问，请重新登录");
			} catch (Exception e1) {
				Reuse.respond(res, null, e1);
			}
		} else {
			Reuse.respond(res, null, e);
		}
	}
}
