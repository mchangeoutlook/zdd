package com.zdd.bdc.servlets;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Bizvalidauth;
 
@SuppressWarnings("serial")
@WebServlet("/u/*")
@MultipartConfig(fileSizeThreshold=1024*100, 	// 100 KB 
                 maxFileSize=1024*100,      	// 100 KB
                 maxRequestSize=1024*1024*5)   	// 5 MB
public class Upload extends HttpServlet {
     
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    		String accountkey = null;
		if (request.getAttribute("accountkey")!=null) {
			accountkey = request.getAttribute("accountkey").toString();
		}
		Bizparams bizp = new Bizparams(request.getParameter("loginkey"), accountkey, request.getAttribute("ip").toString(), request.getHeader("User-Agent"));
		for (Part part : request.getParts()) {
			String contentdisp = part.getHeader("content-disposition");
			String[] tokens = contentdisp.split(";");
			String name = null;
	        for (String token : tokens) {
	        	   	if (token.trim().startsWith("name=")) {
	        	   		name = token.substring(token.indexOf("=") + 2, token.length()-1);
	        	   		break;
	    		    }
	        }
			if (contentdisp.contains("filename=")) {
				bizp.add(name, part.getInputStream());
			} else {
				bizp.add(name, request.getParameter(name));
			}
		}
		Bizvalidauth.go(bizp, request.getRequestURI(), response);
		
    }
 
}