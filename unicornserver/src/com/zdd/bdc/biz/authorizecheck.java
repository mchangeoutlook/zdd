package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class authorizecheck implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetaccountkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetactioncode", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetresourceid", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		if (authorize.authcheck(bizp.getext("targetaccountkey"), bizp.getext("targetresourceid"), bizp.getext("targetactioncode"))) {
			returnvalue.put("auth", "yes");
		} else {
			returnvalue.put("auth", "no");
		}
		return returnvalue;
	}

}