package com.tenotenm.yanxin.servlets;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tenotenm.yanxin.entities.Yxlogin;
import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Dataclient;
import com.zdd.bdc.client.biz.Fileclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/check/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 400, // 400 KB
		maxFileSize = 1024 * 400, // 400 KB
		maxRequestSize = 1024 * 500) // 500 KB
public class Upload extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Yxlogin yxlogin = (Yxlogin)request.getAttribute(Yxlogin.class.getSimpleName());
			String eid = null;//Guard.geteid(request);
			String companykey = request.getParameter("companykey");
			if (companykey == null || companykey.trim().isEmpty()) {
				throw new Exception("缺少参数companykey");
			}

			String filekey = Bigclient.newbigdatakey();
			String filefolder = "elogopic-" + eid;

			for (Part part : request.getParts()) {
				String contentdisp = part.getHeader("content-disposition");
				if (contentdisp.contains("filename=")) {
					if (part.getSize() > 0) {
						Fileclient.getinstance(filefolder).write("bigfileto", filekey, part.getInputStream());
						break;
					} else {
						throw new Exception("请选择小于400k的图片");
					}
				}
			}
			String picpath = "/" + filefolder + "/" + filekey;
			try {
				//Dataclient.getinstance(Static.namespace(eid), Clientcompany.tablename).key(companykey)
				//		.add4create(Clientcompany.col_elogo, picpath, Clientcompany.col_elogo_extra).create();
			} catch (Exception e) {
				if (e.getMessage().contains(STATIC.DUPLICATE)) {
				//	Dataclient.getinstance(Static.namespace(eid), Clientcompany.tablename).key(companykey)
				//			.add4modify(Clientcompany.col_elogo, picpath).modify();
				} else {
					throw e;
				}
			}
			Map<String, String> ret = new Hashtable<String, String>();
			ret.put("companykey", companykey);
			ret.put("picpath", picpath);
			Write.newdayiscoming(yxlogin, true);
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}

	}


	private static byte[] compress2(InputStream is) throws Exception {
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
			param.setCompressionQuality(0.2f);
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
}