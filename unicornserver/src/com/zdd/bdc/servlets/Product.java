package com.zdd.bdc.servlets;
 
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
 
@SuppressWarnings("serial")
@WebServlet("/p")
@MultipartConfig(fileSizeThreshold=1024*100, 	// 100 KB 
                 maxFileSize=1024*100,      	// 100 KB
                 maxRequestSize=1024*1024*5)   	// 5 MB
public class Product extends HttpServlet {
	
	private static final String UPLOAD_DIR = "tempuploads";
     
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
         Files.createDirectories(Paths.get(uploadFilePath));
        
        String fileName = null;
        //Get all the parts from request and write it to the file on server
        for (Part part : request.getParts()) {
            fileName = getFileName(part);
            if (fileName!=null&&!fileName.trim().isEmpty()) {
            part.write(uploadFilePath + File.separator + fileName);
            }
        }
 
        request.setAttribute("message", fileName + " File uploaded successfully!");
        Map<String, String> a = new HashMap<String, String>();
        a.put("test", "1111");
        response.getWriter().print(new ObjectMapper().writeValueAsString(a));
    }
 
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
        	System.out.println(token);
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
}