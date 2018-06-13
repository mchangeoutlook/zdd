package com.zdd.bdc.servlets;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Bizvalidauth;
 
@SuppressWarnings("serial")
@WebServlet("/n/*")
public class Normal extends HttpServlet {
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		String accountkey = null;
		if (request.getAttribute("accountkey")!=null) {
			accountkey = request.getAttribute("accountkey").toString();
		}
		Bizparams bizp = new Bizparams(request.getParameter("loginkey"), accountkey, request.getAttribute("ip").toString(), request.getHeader("User-Agent"));
		for (String name:request.getParameterMap().keySet()) {
			bizp.add(name, request.getParameter(name));
		}
		Bizvalidauth.go(bizp, request.getRequestURI(), response);
   }
}