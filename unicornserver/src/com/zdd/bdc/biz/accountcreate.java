package com.zdd.bdc.biz;

import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class accountcreate implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
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
		System.out.println(bizp.getext("login"));
		if (!bizp.getext("repasswd").equals(bizp.getext("passwd"))){
			throw new Exception("wrongpasswd");
		}
		if (!Indexclient.getinstance("unicorn", bizp.getext("login")).filters(1).add("account").readunique().isEmpty()){
			throw new Exception("duplicate");
		}
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bizp.getext("passwd").getBytes());
		String md5inputpasswd = new String(md.digest(), "UTF-8");
		String accountkey = Textclient.getinstance("unicorn", "account").columnvalues(4)
				.add4create("login", bizp.getext("login"), 100).add4create("passwd", md5inputpasswd, 100).create();
		Indexclient.getinstance("unicorn", bizp.getext("login")).filters(1).add("account").createunique(accountkey);
		return returnvalue;
	}

}