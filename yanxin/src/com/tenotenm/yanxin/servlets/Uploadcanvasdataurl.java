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
			String filefolder = Reuse.yyyyMMdd(today);
			String filekey = null;
			String canvasdataurl = request.getParameter("canvasdataurl");
			if (canvasdataurl == null) {
				throw new Exception("缺少canvasdataurl");
			} else {
				boolean vailddataurl=canvasdataurl.startsWith("data:image/")&&canvasdataurl.contains(";base64,");
				if (!vailddataurl) {
					throw new Exception("缺少canvasdataurl");
				}
			}
			canvasdataurl = canvasdataurl.split(";base64,")[1];
			byte[] contentData = canvasdataurl.getBytes();
			byte[] decodedData = Base64.decodeBase64(contentData);
			if (decodedData.length > 204800) {
				Map<String, Object> ret = new Hashtable<String, Object>();
				ret.put("compressed", "fail");
				ret.put("reason", decodedData.length);
				Reuse.respond(response, ret, null);
			} else {
				filekey = Bigclient.newbigdatakey();
				try {
					Files.write(Paths.get(filekey), decodedData, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
					Fileclient.getinstance(filefolder).write(Reuse.namespace_bigfileto, filekey,
							Files.newInputStream(Paths.get(filekey)));
					String yanxinkey = Bizutil.createyanxin(yxaccount, yxlogin, filefolder, filekey, today);

					Map<String, Object> ret = new Hashtable<String, Object>();
					ret.put("onetimekeyphoto", Bizutil.onetimekey(null, yanxinkey));
					ret.put("compressed", "true");
					Integer m = Bizutil.newdaycomingminutes(yxlogin);
					if (m != null) {
						ret.put("newdaycomingminutes",
								"健康的身体需要充足的睡眠，请尽快结束今天的日记，" + m + "分钟后你将不能继续修改今天的日记，零点后你需要重新登录并开启第二天的日记");
					}
					Reuse.respond(response, ret, null);
				} finally {
					Paths.get(filekey).toFile().delete();
				}
			}
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}

}