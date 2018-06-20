package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shoployeeapprove implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shoployeekey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
		
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("shopkey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		approve(bizp.getext("shoployeekey"));
		return returnvalue;
	}
	
	public static void approve(String shoployeekey) throws Exception {
		Textclient.getinstance("unicorn", "shoployee").key(shoployeekey).columnvalues(1).add4modify("status", "2").modify();
	}

}