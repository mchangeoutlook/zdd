package com.tenotenm.yanxin.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yanxin;
import com.tenotenm.yanxin.util.Bizutil;
import com.tenotenm.yanxin.util.Downloading;
import com.zdd.bdc.client.biz.Fileclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/download")
public class Download extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Origin", "*");

		try {
			String onetimekey=req.getParameter("onetimekey");
			String yanxinkey  =Bizutil.onetimekey(onetimekey, null);
			if (yanxinkey!=null) {
				Yanxin yxyanxin = new Yanxin();
				yxyanxin.read(yanxinkey);
				String[] photofolderkey = yxyanxin.getPhoto().split("/");
				if (photofolderkey.length==2) {
					String thefolderonserver=photofolderkey[0];
					String filekey = photofolderkey[1];
					Fileclient.getinstance(thefolderonserver).read("bigfilefrom", filekey, new Downloading(res));
				}
			} else {
				res.sendRedirect("/protectphoto.htm");
			}
		} catch (Exception e) {
			if (e.getMessage()==null||e.getMessage()!=null&&!e.getMessage().contains(STATIC.INVALIDKEY)&&!e.getMessage().contains("NoSuchFileException")) {
				throw new IOException(e);
			}
		}
	}

}