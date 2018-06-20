package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class comployeerp implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("comployeekey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("companykey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("rewardpoints", Ibiz.VALIDRULE_MIN_MAX_PREFIX+1+Ibiz.SPLITTER+1000000);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("companykey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		returnvalue.put("rewardpoints", Textclient.getinstance("unicorn", "comployee").key(bizp.getext("comployeekey")).columnamounts(1).add4increment("rp", Long.parseLong(bizp.getext("rewardpoints"))).increment());
		return returnvalue;
	}

}