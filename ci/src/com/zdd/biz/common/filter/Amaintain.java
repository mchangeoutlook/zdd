package com.zdd.biz.common.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Amaintain implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse)arg1;
		try {
			Path maintainfolder = Paths.get("maintain");
			Files.createDirectories(maintainfolder);
			String d = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String file = arg0.getParameter("target")+d;
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			if (Files.exists(maintainfolder.resolve(file))) {
				result.put("maintain", 1);
				result.put("date", d);
			} else {
				result.put("maintain", 0);
			}
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
				
    }
	
}
