package com.zdd.bdc.biz;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class accountlogin implements Ibiz {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String login = request.getParameter("login");
		String passwd = request.getParameter("passwd");
		String repasswd = request.getParameter("repasswd");
		String loginkey = request.getParameter("loginkey");
		Map<String, Object> returnvalue = new HashMap<String, Object>();
		try {
			if (loginkey != null) {
				if (repasswd != null && !repasswd.isEmpty() && repasswd.equals(passwd)) {
					String accountkey = Indexclient.getinstance("unicorn", login).readunique();
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(passwd.getBytes());
					String md5inputpasswd = new String(md.digest(), "UTF-8");
					Textclient.getinstance("unicorn", "account").key(accountkey).columnvalues(1)
					.add4modify("passwd", md5inputpasswd).modify();
					returnvalue.put("state", 0);
					returnvalue.put("changepasswd", "yes");
				} else if (repasswd != null && !repasswd.equals(passwd)){
					throw new Exception("wrongpasswd");
				} else {
					Textclient.getinstance("unicorn", "login").key(loginkey).columnvalues(2)
							.add4modify("lastactime", String.valueOf(System.currentTimeMillis()))
							.add4modify("outime", String.valueOf(System.currentTimeMillis())).modify();
					returnvalue.put("state", 0);
					returnvalue.put("logout", "yes");
				}
			} else {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(passwd.getBytes());
				String md5inputpasswd = new String(md.digest(), "UTF-8");
				String accountkey = Indexclient.getinstance("unicorn", login).readunique();
				if (accountkey.isEmpty()) {
					if (repasswd == null || repasswd.isEmpty() || !repasswd.equals(passwd)) {
						throw new Exception("notexist");
					}
					accountkey = Textclient.getinstance("unicorn", "account").columnvalues(4)
							.add4create("login", login, 100).add4create("passwd", md5inputpasswd, 100)
							.add4create("rp", "0", 20).add4create("authself", "1111", 4).create();
					Indexclient.getinstance("unicorn", login).createunique(accountkey);
				} else {
					if (!md5inputpasswd.equals(Textclient.getinstance("unicorn", "account").key(accountkey).columns(1)
							.add("passwd").read().get("passwd"))) {
						throw new Exception("wrongpasswd");
					}
				}

				String ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = request.getRemoteAddr();
				}

				loginkey = Textclient.getinstance("unicorn", "login").columnvalues(7)
						.add4create("accountkey", accountkey, 100).add4create("ip", ipAddress, 25)
						.add4create("client", request.getHeader("User-Agent"), 1000)
						.add4create("intime", String.valueOf(System.currentTimeMillis()), 20)
						.add4create("lastactime", String.valueOf(System.currentTimeMillis()), 20)
						.add4create("outime", "", 20).add4create("expiretime", "", 20).create();
				long logintimes = Textclient.getinstance("unicorn", "account").key(accountkey).columnamounts(1)
						.add4increment("logintimes", 1).increment().get("logintimes");
				Indexclient.getinstance("unicorn", accountkey).filters(1).add("loginhistory").create(loginkey, logintimes / 100);
				
				returnvalue.put("state", 0);
				returnvalue.put("loginkey", loginkey);
			}
		} catch (Exception e) {
			returnvalue.put("state", 1);
			returnvalue.put("reason", e.getMessage());
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			returnvalue.put("detail", errors.toString());
		}
		response.getWriter().print(new ObjectMapper().writeValueAsString(returnvalue));
	}

	@Override
	public Map<String, String> validrules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String auth(Bizparams bizp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> process() {
		// TODO Auto-generated method stub
		return null;
	}

}