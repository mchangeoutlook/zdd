package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class comployeeapply implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("companykey", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		apply(bizp.getext("companykey"), bizp.getaccountkey());
		return returnvalue;
	}
	
	public static String apply(String companykey, String accountkey) throws Exception {
		String comployeekey = Textclient.getinstance("unicorn", "comployee").columnvalues(3).add4create("account", accountkey, 100).add4create("company", companykey, 100).add4create("status", "1", 1).create();
		
		long numofcomployees = Textclient.getinstance("unicorn", "company").key(companykey).columnamounts(1).add4increment("numofcomployees", 1).increment().get("numofcomployees");
		Indexclient.getinstance("unicorn", companykey).filters(1).add("comployees").create(comployeekey, numofcomployees / 100);
		
		long numofcompanies = Textclient.getinstance("unicorn", "account").key(accountkey).columnamounts(1).add4increment("numofcompanies", 1).increment().get("numofcompanies");
		Indexclient.getinstance("unicorn", accountkey).filters(1).add("companies").create(comployeekey, numofcompanies / 100);
		
		return comployeekey;
	}

}