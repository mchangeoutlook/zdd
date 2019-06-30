package com.tenotenm.yanxin.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import com.tenotenm.yanxin.util.Secret;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Fileclient;

@SuppressWarnings("serial")
@WebServlet("/cexpired/upload")
@MultipartConfig(maxFileSize = 1024 * 100, // 100 KB
		maxRequestSize = 1024 * 110) // 100 KB + 10k
public class Upload extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin) request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount) request.getAttribute(Yxaccount.class.getSimpleName());
			Date today = new Date();
			String photofolder = Reuse.yyyyMMdd(today);
			String photokey = null;
			for (Part part : request.getParts()) {
				String contentdisp = part.getHeader("content-disposition");
				if (contentdisp.contains("filename=")) {
					if (part.getSize() > 0) {
						try {
							InputStream imageis = null;
							try {
								imageis = part.getInputStream();
								BufferedImage bi = ImageIO.read(imageis);
								if (bi == null) {
									throw new Exception(Reuse.msg_hint+"不能上传图片以外的其它文件");
								} else {
									bi = null;
								}
							} finally {
								if (imageis != null) {
									imageis.close();
								}
							}
							photokey = Bigclient.newbigdatakey();
							ByteArrayOutputStream output = new ByteArrayOutputStream();
							InputStream is = part.getInputStream();
							try{
								byte[] buffer = new byte[4096];
							    int n = 0;
							    while (-1 != (n = is.read(buffer))) {
							        output.write(buffer, 0, n);
							    }
							    Files.write(Paths.get(photokey), Secret.enc(output.toByteArray()), StandardOpenOption.CREATE, StandardOpenOption.SYNC) ;
							    
								Fileclient.getinstance(photofolder).write(Reuse.namespace_yanxin, Reuse.app_file, photokey,
									Files.newInputStream(Paths.get(photokey)));
							}finally {
								if (Files.exists(Paths.get(photokey))) {
									Paths.get(photokey).toFile().delete();
								}
								output.close();
								is.close();
							}
						} finally {
							part.delete();
						}
						break;
					}
				}
			}
			if (photokey == null) {
				throw new Exception(Reuse.msg_hint+"请上传图片");
			}
			String yanxinkey = Bizutil.createyanxin(yxaccount, yxlogin, photofolder, photokey, photokey, today);

			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("onetimekeyphoto", Bizutil.onetimekey(null, yanxinkey));
			String hint = Bizutil.newdaycominghint(yxlogin);
			if (hint!=null) {
				ret.put("newdaycomingminutes", hint);
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}

}