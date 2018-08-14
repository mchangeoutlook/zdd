package com.zdd.bdc.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.bdc.biz.Fileclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Downloadpng;

@SuppressWarnings("serial")
@WebServlet("/d/*")
public class Download extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			String key = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
			Map<String, String> res = Textclient.getinstance("unicorn", "png").key(key).columns(3)
					.add("status").add("type").add("path").read();
			if ("oa97341Ulhsd!fhaskl$dfDwesd3f".equals(request.getParameter("reviewpicupload"))||"1".equals(res.get("status"))) {
				Fileclient.getinstance(res.get("path")).read(key, new Downloadpng(os));
			} else if ("0".equals(res.get("status"))) {
				if ("content".equals(res.get("type"))) {
					os.write(Files.readAllBytes(Paths.get("reviewcontent.png")));
					os.flush();
				} else {
					os.write(Files.readAllBytes(Paths.get("reviewhead.png")));
					os.flush();
				}
			} else {
				os.write("rejected".getBytes());
				os.flush();
			}
		} catch (Exception e) {
			Map<String, Object> returnvalue = new HashMap<String, Object>();
			returnvalue.put("state", 1);
			returnvalue.put("reason", e.getMessage());
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			returnvalue.put("detail", errors.toString());
			os.write(new ObjectMapper().writeValueAsBytes(returnvalue));
			os.flush();
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}
}