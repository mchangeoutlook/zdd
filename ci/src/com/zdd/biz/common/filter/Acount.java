package com.zdd.biz.common.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Acount implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			long cnt = 0l;
			Path countfolder = Paths.get("count");
			Files.createDirectories(countfolder);
			if (Files.exists(countfolder.resolve("validentries"))) {
				String validentries = Files.readAllLines(countfolder.resolve("validentries"),Charset.forName("UTF-8")).get(0);
				if (validentries!=null&&!validentries.trim().isEmpty()) {
					if (req.getParameter("target")!=null&&!req.getParameter("target").trim().isEmpty()) {
			        		String file = req.getParameter("target").trim();
			        		if (validentries.contains(","+file+",")) {
			        			if (req.getParameter("increase")!=null) {
				        			Files.write(countfolder.resolve(file), new byte[1], StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				        		}
				        		if (Files.exists(countfolder.resolve(file))) {
				        			cnt = Files.size(countfolder.resolve(file));
				        		} else {
				        			cnt = 0;
					        	}
				        
						}
					}
				}
			}
			result.put("cnt", cnt);
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
				
    }
	
}
