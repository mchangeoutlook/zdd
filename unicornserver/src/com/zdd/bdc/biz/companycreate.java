package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class companycreate implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("companyname", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		if (!Indexclient.getinstance("unicorn", bizp.getext("companyname")).filters(1).add("company").readunique().isEmpty()){
			throw new Exception("duplicate");
		}
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		String companykey = Textclient.getinstance("unicorn", "company").columnvalues(4)
				.add4create("loginkey", bizp.getloginkey(), 100).add4create("admin", bizp.getaccountkey(), 100)
				.add4create("name", bizp.getext("companyname"), 100).create();
		Indexclient.getinstance("unicorn", bizp.getext("companyname")).filters(1).add("company").createunique(companykey);
		
		String comployeekey = comployeeapply.apply(companykey, bizp.getaccountkey());
		comployeeapprove.approve(comployeekey);
		
		authorize.authassign(bizp.getaccountkey(), companykey, "authorize");
		
		return returnvalue;
	}

}