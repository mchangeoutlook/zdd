package com.tenotenm.yanxin.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Fileclient;

@SuppressWarnings("serial")
@WebServlet("/cexpired/upload")
@MultipartConfig(maxFileSize = 1024 * 200, // 200 KB
		maxRequestSize = 1024 * 210) // 200 KB + 10k
public class Upload extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin) request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			Date today = new Date();
			String filefolder = Reuse.yyyyMMdd(today);
			String filekey = null;
			for (Part part : request.getParts()) {
				String contentdisp = part.getHeader("content-disposition");
				if (contentdisp.contains("filename=")) {
					if (part.getSize() > 0) {
						try {
							InputStream is = null;
							try {
								is = part.getInputStream();
								BufferedImage bi = ImageIO.read(is);
								if (bi == null) {
									throw new Exception("不能上传图片以外的其它文件");
								} else {
									bi = null;
								}
							} finally {
								if (is != null) {
									is.close();
								}
							}
							filekey = Bigclient.newbigdatakey();
							Fileclient.getinstance(filefolder).write(Reuse.namespace_bigfileto, filekey,
									part.getInputStream());
						} finally {
							part.delete();
						}
						break;
					}
				}
			}
			if (filekey == null) {
				throw new Exception("请上传图片");
			}
			String yanxinkey = Bizutil.createyanxin(yxaccount, yxlogin, filefolder, filekey, today);

			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("onetimekeyphoto", Bizutil.onetimekey(null, yanxinkey));
			ret.put("compressed", "false");
			Integer m = Bizutil.newdaycomingminutes(yxlogin);
			if (m != null) {
				ret.put("newdaycomingminutes", "健康的身体需要充足的睡眠，请尽快结束今天的日记，" + m + "分钟后你将不能继续修改今天的日记，零点后你需要重新登录并开启第二天的日记");
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}

}