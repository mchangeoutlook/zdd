package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tenotenm.yanxin.entities.Yanxin;
import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Fileclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/cexpired/upload")
@MultipartConfig(maxFileSize = 1024 * 4000, // 4 MB
		maxRequestSize = 1024 * 4010) // 4 MB + 10k
public class Upload extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			Yxaccount yxaccount = (Yxaccount)request.getAttribute(Yxaccount.class.getSimpleName());
			Date today = new Date();
			String filefolder = Reuse.yyyyMMdd(today);
			String filekey=null;
			long compressedsize = 0;
			for (Part part : request.getParts()) {
				String contentdisp = part.getHeader("content-disposition");
				if (contentdisp.contains("filename=")) {
					if (part.getSize() > 0) {
						filekey = Bigclient.newbigdatakey();
						Path compressed = null;
						try {
							if (part.getSize()>100000) {
								compressed = Paths.get("compressed"+filekey);
								float rate = 0.2f;
								if (part.getSize()<1000000){
									rate = 0.5f;
								} else if (part.getSize()<2000000){
									rate = 0.4f;
								} else if (part.getSize()<3000000){
									rate = 0.3f;
								} else {
									rate = 0.2f;
								}
								Files.write(compressed, Bizutil.compress(part.getInputStream(), rate), StandardOpenOption.CREATE, StandardOpenOption.SYNC);
								compressedsize = Files.size(compressed);
								Fileclient.getinstance(filefolder).write(Reuse.namespace_bigfileto, filekey, Files.newInputStream(compressed));
							} else {
								Fileclient.getinstance(filefolder).write(Reuse.namespace_bigfileto, filekey, part.getInputStream());
							}
						}finally {
							try {
								part.delete();
							}finally {
								if (compressed!=null) {
									compressed.toFile().delete();
								}
							}
						}
						break;
					}
				}
			}
			if (filekey==null) {
				throw new Exception("请浏览本地小于4M的图片(正方形图片将获得最佳显示效果),大于100K的图片将会被压缩存储，压缩后图片质量有所损失");
			}
			Yanxin yx = Bizutil.readyanxin(yxaccount, today);
			if (yx==null) {
				yx = new Yanxin();
				yx.setKey(Bigclient.newbigdatakey());
				yx.setPhoto(filefolder+"/"+filekey);
				yx.setContent("");
				yx.setLocation("");
				yx.setWeather("");
				yx.setUniquekeyprefix(yxaccount.getYxyanxinuniquekeyprefix());
				yx.setYxloginkey(yxlogin.getKey());
				try {
					yx.createunique(null, Bizutil.yanxinkey(yxaccount, today));
				}catch(Exception e) {
					if (e.getMessage()!=null&&e.getMessage().contains(STATIC.DUPLICATE)) {
						yx.readunique(Bizutil.yanxinkey(yxaccount, today));
						if (yx.getPhoto()!=null&&!yx.getPhoto().isEmpty()) {
							String[] folderkey=yx.getPhoto().split("/");
							if (folderkey.length==2) {
								Fileclient.getinstance(folderkey[0]).delete(Reuse.namespace_bigfileto, folderkey[1]);
							}
						}
						yx.setPhoto(filefolder+"/"+filekey);
						yx.setYxloginkey(yxlogin.getKey());
						yx.modify(null);
					} else {
						throw e;
					}
				}
			}

			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("photo", filefolder+"/"+filekey);
			ret.put("compressedsize", compressedsize);
			Integer m = Bizutil.newdaycomingminutes(yxlogin);
			if (m!=null) {
				ret.put("newdaycomingminutes", "健康的身体需要充足的睡眠，请尽快结束今天的日记，"+m+"分钟后你将不能继续修改今天的日记，零点后你需要重新登录并开启第二天的日记");
			}
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}


}