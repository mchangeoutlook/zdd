package com.zdd.biz.pe.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.biz.pe.Judge;
import com.zdd.biz.pe.Judgepool;

public class Acount implements Filter {

	public static Map<String, Judge> JUDGES = new HashMap<String, Judge>();
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse)arg1;
		try {
			Path countfolder = Paths.get("count");
			Files.createDirectories(countfolder);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "yes");
			result.put("cnt", Judgepool.IDJUDGE.size());
	      	res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}catch(Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("status", "no");
			result.put("reason", e.getMessage());
			res.getWriter().write(new ObjectMapper().writeValueAsString(result));
		}
				
    }
	
}
