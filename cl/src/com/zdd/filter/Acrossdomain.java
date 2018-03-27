package com.zdd.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class Acrossdomain implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse)arg1;
        res.setHeader("Access-Control-Allow-Origin","*");
		
		arg2.doFilter(arg0, res);
	}
	
}
