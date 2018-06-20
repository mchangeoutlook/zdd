package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class comshopapply implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("companykey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("companykey");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		apply(bizp.getext("companykey"), bizp.getaccountkey());
		return returnvalue;
	}
	
	public static String apply(String companykey, String shopkey) throws Exception {
		String shopanykey = Textclient.getinstance("unicorn", "shopany").columnvalues(3).add4create("shop", shopkey, 100).add4create("company", companykey, 100).add4create("status", "1", 1).create();
		
		long numofshops = Textclient.getinstance("unicorn", "company").key(companykey).columnamounts(1).add4increment("numofshops", 1).increment().get("numofshops");
		Indexclient.getinstance("unicorn", companykey).filters(1).add("shops").create(shopanykey, numofshops / 100);
		
		long numofcompanies = Textclient.getinstance("unicorn", "shop").key(shopkey).columnamounts(1).add4increment("numofcompanies", 1).increment().get("numofcompanies");
		Indexclient.getinstance("unicorn", shopkey).filters(1).add("companies").create(shopanykey, numofcompanies / 100);
		
		return shopanykey;
	}

}