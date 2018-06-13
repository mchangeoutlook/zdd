package com.zdd.bdc.biz;

import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class accountlogin implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("login", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("passwd", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String actioncode() {
		return null;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bizp.getext("passwd").getBytes());
		String md5inputpasswd = new String(md.digest(), "UTF-8");
		String accountkey = Indexclient.getinstance("unicorn", bizp.getext("login")).filters(1).add("account").readunique();
		if (accountkey.isEmpty()) {
			throw new Exception("notexist");
		}
		if (!md5inputpasswd.equals(Textclient.getinstance("unicorn", "account").key(accountkey).columns(1)
					.add("passwd").read().get("passwd"))) {
			throw new Exception("wrongpasswd");
		}
		String loginkey = Textclient.getinstance("unicorn", "login").columnvalues(7)
				.add4create("accountkey", accountkey, 100).add4create("ip", bizp.getip(), 25)
				.add4create("client", bizp.getuseragent(), 1000)
				.add4create("intime", String.valueOf(System.currentTimeMillis()), 20)
				.add4create("lastactime", String.valueOf(System.currentTimeMillis()), 20)
				.add4create("outime", "", 20).add4create("expiretime", "", 20).create();
		long logintimes = Textclient.getinstance("unicorn", "account").key(accountkey).columnamounts(1)
				.add4increment("logintimes", 1).increment().get("logintimes");
		Indexclient.getinstance("unicorn", accountkey).filters(1).add("loginhistory").create(loginkey, logintimes / 100);
		returnvalue.put("loginkey", loginkey);
		return returnvalue;
	}

}