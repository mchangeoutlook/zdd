package com.tenotenm.yanxin.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.entities.Yxaccount;
import com.tenotenm.yanxin.util.Bizutil;

@WebFilter("/cexpired/*")
public class Checkexpired implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse res = (HttpServletResponse) arg1;
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Origin", "*");
		try {
			Bizutil.commoncheck(req);
			
			Bizutil.checkaccountexpired((Yxaccount)req.getAttribute(Yxaccount.class.getSimpleName()));
			
			arg2.doFilter(req, res);
			
		} catch (Exception e) {
			Bizutil.commoncheckexception(req, res, e);
		}
	}


}
