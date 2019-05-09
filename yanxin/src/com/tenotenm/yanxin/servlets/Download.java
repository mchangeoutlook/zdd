package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.util.Downloading;
import com.zdd.bdc.client.biz.Fileclient;

@SuppressWarnings("serial")
@WebServlet("/cexpired/download")
public class Download extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Origin", "*");

		OutputStream os = null;
		try {
			os = res.getOutputStream();
			String thefolderonserver=req.getParameter("folder");
			String filekey = req.getParameter("filekey");
			Fileclient.getinstance(thefolderonserver).read("bigfilefrom", filekey, new Downloading(os));
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}