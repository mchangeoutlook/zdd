package com.zdd.bdc.biz;

import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class accountchangepasswd implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("login", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("passwd", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("repasswd", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		if (!bizp.getext("repasswd").equals(bizp.getext("passwd"))){
			throw new Exception("wrongpasswd");
		}
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		String accountkey = Indexclient.getinstance("unicorn", bizp.getext("login")).filters(1).add("account").readunique();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bizp.getext("passwd").getBytes());
		String md5inputpasswd = new String(md.digest(), "UTF-8");
		Textclient.getinstance("unicorn", "account").key(accountkey).columnvalues(1)
		.add4modify("passwd", md5inputpasswd).modify();
		return returnvalue;
	}
}