package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.tenotenm.yanxin.util.Secret;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Fileclient;

@SuppressWarnings("serial")
@WebServlet("/cexpired/uploadcanvas")
public class Uploadcanvasdataurl extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin) request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			Date today = new Date();
			String photofolder = Reuse.yyyyMMdd(today);
			String photocanvasdataurl = request.getParameter("photocanvasdataurl");
			String photosmallcanvasdataurl = request.getParameter("photosmallcanvasdataurl");
			if (photocanvasdataurl == null) {
				throw new Exception("缺少photocanvasdataurl");
			} else {
				boolean vailddataurl=photocanvasdataurl.startsWith("data:image/")&&photocanvasdataurl.contains(";base64,");
				if (!vailddataurl) {
					throw new Exception("无效photocanvasdataurl");
				}
			}
			if (photosmallcanvasdataurl == null) {
				throw new Exception("缺少photosmallcanvasdataurl");
			} else {
				boolean vailddataurl=photosmallcanvasdataurl.startsWith("data:image/")&&photosmallcanvasdataurl.contains(";base64,");
				if (!vailddataurl) {
					throw new Exception("无效photosmallcanvasdataurl");
				}
			}
			String photokey = storephoto(photocanvasdataurl, photofolder, 102400);
			if (photokey==null) {
				Map<String, Object> ret = new Hashtable<String, Object>();
				ret.put("compressed", "fail");
				ret.put("reason", "图片超过102400");
				Reuse.respond(response, ret, null);
			} else {
				String photosmallkey = storephoto(photocanvasdataurl, photofolder, 51200);
				if (photosmallkey==null) {
					Map<String, Object> ret = new Hashtable<String, Object>();
					ret.put("compressed", "fail");
					ret.put("reason", "图片过大");
					Reuse.respond(response, ret, null);
				} else {
					String yanxinkey = Bizutil.createyanxin(yxaccount, yxlogin, photofolder, photokey, photosmallkey, today);
					Map<String, Object> ret = new Hashtable<String, Object>();
					ret.put("onetimekeyphoto", Bizutil.onetimekey(null, yanxinkey));
					ret.put("compressed", "true");
					String hint = Bizutil.newdaycominghint(yxlogin);
					if (hint!=null) {
						ret.put("newdaycomingminutes", hint);
					}
					Reuse.respond(response, ret, null);
				}
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}
	
	private static String storephoto(String canvasdataurl, String filefolder, long maxsize) throws Exception {
		canvasdataurl = canvasdataurl.split(";base64,")[1];
		byte[] contentData = canvasdataurl.getBytes();
		byte[] decodedData = Base64.decodeBase64(contentData);
		if (decodedData.length > maxsize) {
			return null;
		} else {
			String filekey = Bigclient.newbigdatakey();
			try {
				Files.write(Paths.get(filekey), Secret.enc(decodedData), StandardOpenOption.CREATE, StandardOpenOption.SYNC);
				Fileclient.getinstance(filefolder).write(Reuse.namespace_bigfileto, filekey,
						Files.newInputStream(Paths.get(filekey)));
				return filekey;
			}finally {
				if (Files.exists(Paths.get(filekey))) {
					Paths.get(filekey).toFile().delete();
				}
			}
		}
	}

}