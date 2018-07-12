package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shoprodapprove implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shoprodkey", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("shopkey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		Textclient.getinstance("unicorn", "prod").key(bizp.getext("shoprodkey")).columnvalues(1)
		.add4modify("status","1").modify();
		return returnvalue;
	}

}