package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shoprodmodify implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shoprodkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("headimg", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("contentimgs", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("prodname", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("prodrp", Ibiz.VALIDRULE_MIN_MAX_PREFIX+1+Ibiz.SPLITTER+1000000);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("shopkey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		if (!Indexclient.getinstance("unicorn", bizp.getext("prodname")).filters(1).add("prod").readunique().isEmpty()){
			throw new Exception("duplicate");
		}
		Textclient.getinstance("unicorn", "prod").key(bizp.getext("shoprodkey")).columnvalues(5)
				.add4modify("status", "0")
				.add4modify("name", bizp.getext("prodname")).add4modify("rp", bizp.getext("prodrp"))
				.add4modify("headimg", bizp.getext("headimg")).add4modify("contentimgs", bizp.getext("contentimgs")).modify();
		return returnvalue;
	}

}