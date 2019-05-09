package com.tenotenm.yanxin.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Ipdeny;
import com.tenotenm.yanxin.entities.Iplimit;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.entities.Yanxin;
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

	public static void logout(Yxlogin yxlogin) throws Exception {
		if (!yxlogin.getIslogout()) {
			yxlogin.setIslogout(true);
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());
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
					&& ipd.getNewaccounts() >= Reuse.getlongvalueconfig("everyday.everyip.newaccounts.max")) {
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

	public static Yanxin readyanxin(Yxaccount yxaccount, String key) throws Exception {
		Yanxin yxyanxin = new Yanxin();
		yxyanxin.read(key);
		if (!yxyanxin.getUniquekeyprefix().equals(yxaccount.getYxyanxinuniquekeyprefix())) {
			throw new Exception("无权查看别人的日记");
		}
		return yxyanxin;
	}

	public static Map<String, String> convert(Yanxin yanxin) {
		Map<String, String> ret = new Hashtable<String, String>();
		if (yanxin == null) {
			return ret;
		}
		ret.put("content", yanxin.getContent());
		ret.put("key", yanxin.getKey());
		ret.put("location", yanxin.getLocation());
		ret.put("photo", yanxin.getPhoto());
		ret.put("weather", yanxin.getWeather());
		ret.put("timecreate", Reuse.yyyyMMdd(yanxin.getTimecreate()));
		return ret;
	}

	public static String yanxinkey(Yxaccount yxaccount, Date day) {
		return yxaccount.getYxyanxinuniquekeyprefix() + "-" + Reuse.yyyyMMdd(day);
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
				&& Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("admins")
						.contains(yxaccount.getKey());
	}

	public static void refreshadminaccount(Yxaccount yxaccount) throws Exception {
		if (isadmin(yxaccount)) {
			if (yxaccount.getDaystogive() < Reuse.getlongvalueconfig("days.togive.max")) {
				yxaccount.setDaystogive4increment(
						Reuse.getlongvalueconfig("days.togive.max") - yxaccount.getDaystogive());
				yxaccount.increment(yxaccount.getKey());
			}
			if (yxaccount.getTimeexpire().before(new Date())) {
				Date timeexpire = new Date(new Date().getTime() + Reuse.getdaysmillisconfig("freeuse.days"));
				yxaccount.setTimeexpire(timeexpire);
				yxaccount.setTimeupdate(new Date());
				yxaccount.modify(yxaccount.getKey());
			}
		}
	}

	public static void checkaccountreused(Yxaccount yxaccount) throws Exception {
		if (isfirstlogindenied(yxaccount)) {
			throw new Exception("账号 " + yxaccount.getName() + " 未及时完成首次登录，已被回收");
		}
		if (isreusing(yxaccount)) {
			throw new Exception("账号 " + yxaccount.getName() + " 未及时延长过期时间，已被回收");
		}
	}

	public static void checkaccountavailability(Yxaccount yxaccount) throws Exception {
		if (!isadmin(yxaccount) && isaccountexpired(yxaccount)) {
			throw new Exception(
					"账号 " + yxaccount.getName() + " 已过期，请在" + Reuse.yyyyMMddHHmmss(datedenyreuseaccount(yxaccount))
							+ "之前延长过期时间，否则该账号将被回收，回收后该账号的所有日记和资源都将无法找回");
		}
	}

	public static boolean isaccountexpired(Yxaccount yxaccount) throws Exception {
		Date now = new Date();
		return yxaccount.getTimeexpire().before(now) && now.before(datedenyreuseaccount(yxaccount));
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

	public static byte[] compress(InputStream is, float compressrate) throws Exception {
		ByteArrayOutputStream convertedos = null;
		ByteArrayOutputStream compressedos = null;
		ImageWriter compressedwriter = null;
		try {
			BufferedImage toconvertimage = ImageIO.read(is);
			convertedos = new ByteArrayOutputStream();

			BufferedImage converted = new BufferedImage(toconvertimage.getWidth(), toconvertimage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			converted.createGraphics().drawImage(toconvertimage, 0, 0, Color.BLACK, null);
			ImageIO.write(converted, "jpg", convertedos);

			BufferedImage image = ImageIO.read(new ByteArrayInputStream(convertedos.toByteArray()));

			compressedos = new ByteArrayOutputStream();

			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			compressedwriter = (ImageWriter) writers.next();

			ImageOutputStream ios = ImageIO.createImageOutputStream(compressedos);
			compressedwriter.setOutput(ios);

			ImageWriteParam param = compressedwriter.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(compressrate);// 0.2f
			compressedwriter.write(null, new IIOImage(image, null, null), param);
			return compressedos.toByteArray();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
			try {
				convertedos.close();
			} catch (Exception e) {
			}
			try {
				compressedos.close();
			} catch (Exception e) {
			}
			try {
				compressedwriter.dispose();
			} catch (Exception e) {
			}

		}
	}

	public static void commoncheck(HttpServletRequest req) throws Exception {
		String ip = Reuse.getremoteip(req);
		Bizutil.ipdeny(ip, false);
		Yxlogin yxlogin = new Yxlogin();
		yxlogin.read(req.getParameter("loginkey"));

		if (!ip.equals(yxlogin.getIp()) || !Reuse.getuseragent(req).equals(yxlogin.getUa())) {
			throw new Exception("已启动IP保护，请重新登录");
		}

		if (yxlogin.getIslogout()) {
			throw new Exception("已退出，请重新登录");
		}
		if (System.currentTimeMillis() - yxlogin.getTimeupdate().getTime() > Reuse
				.getsecondsmillisconfig("session.expire.seconds")) {
			throw new Exception("已过期，请重新登录");
		}

		Yxaccount yxaccount = new Yxaccount();
		yxaccount.read(yxlogin.getYxaccountkey());

		if (!yxaccount.getYxloginkey().equals(yxlogin.getKey())) {
			throw new Exception("非法访问，请重新登录");
		}

		if (Reuse.yyyyMMdd(new Date()).equals(Reuse.yyyyMMdd(yxlogin.getTimeupdate()))) {
			yxlogin.setTimeupdate(new Date());
			yxlogin.modify(yxlogin.getKey());
		} else {
			throw new Exception("迎接新的一天，请重新登录");
		}

		Bizutil.refreshadminaccount(yxaccount);

		Bizutil.checkaccountreused(yxaccount);

		req.setAttribute(Yxaccount.class.getSimpleName(), yxaccount);
		req.setAttribute(Yxlogin.class.getSimpleName(), yxlogin);
	}

	public static void commoncheckexception(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException {
		if (e.getMessage() != null && (e.getMessage().contains(STATIC.INVALIDKEY)||e.getMessage().contains(Reuse.NOTFOUND))) {
			try {
				Bizutil.ipdeny(Reuse.getremoteip(req), true);
				throw new Exception("无效访问，请重新登录");
			} catch (Exception e1) {
				Reuse.respond(res, null, e1);
			}
		} else {
			Reuse.respond(res, null, e);
		}
	}
}
