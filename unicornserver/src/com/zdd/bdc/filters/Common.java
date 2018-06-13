package com.zdd.bdc.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.bdc.biz.Configclient;
import com.zdd.bdc.biz.Textclient;

@WebFilter("/*")
public class Common implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
        req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json");
		res.setHeader("Access-Control-Allow-Origin","*");
		String ip = req.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = req.getRemoteAddr();
		}
		req.setAttribute("ip", ip);
		if (req.getParameter("loginkey")!=null&&!req.getParameter("loginkey").trim().isEmpty()) {
			Map<String, Object> returnvalue = new HashMap<String, Object>();
			try {
				Map<String, String> loginstatus = Textclient.getinstance("unicorn", "login")
						.key(req.getParameter("loginkey")).columns(5).add("ip").add("accountkey").add("lastactime").add("expiretime").add("outime").read();
				if (!"".equals(loginstatus.get("outime"))) {
					throw new Exception("loggedout");
				}
				if (!"".equals(loginstatus.get("expiretime"))) {
					throw new Exception("expired");
				}
				if (!ip.equals(loginstatus.get("ip"))||System.currentTimeMillis() - Long.parseLong(loginstatus.get("lastactime"))>Integer.parseInt(Configclient.getinstance("unicorn", "core").read("sessionexpireseconds"))*1000) {
					Textclient.getinstance("unicorn", "login").key(req.getParameter("loginkey")).columnvalues(1)
					.add4modify("expiretime", String.valueOf(System.currentTimeMillis())).modify();
					throw new Exception("expired");
				}
				Textclient.getinstance("unicorn", "login").key(req.getParameter("loginkey")).columnvalues(1)
				.add4modify("lastactime", String.valueOf(System.currentTimeMillis())).modify();
				req.setAttribute("accountkey", loginstatus.get("accountkey"));
			} catch (Exception e) {
				returnvalue.put("state", 1);
				returnvalue.put("reason", e.getMessage());
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				returnvalue.put("detail", errors.toString());
			}
			if (!returnvalue.isEmpty()) {
				res.getWriter().print(new ObjectMapper().writeValueAsString(returnvalue));
				return;
			}
		}
		arg2.doFilter(req, res);
	}
	
}
