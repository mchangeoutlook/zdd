package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class comshopapprove implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopanykey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("companykey", Ibiz.VALIDRULE_NOTEMPTY);
		
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("companykey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		approve(bizp.getext("shopanykey"));
		return returnvalue;
	}
	
	public static void approve(String shopanykey) throws Exception {
		Textclient.getinstance("unicorn", "shopany").key(shopanykey).columnvalues(1).add4modify("status", "2").modify();
	}

}