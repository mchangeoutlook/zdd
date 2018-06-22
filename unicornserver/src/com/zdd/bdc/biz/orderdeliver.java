package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class orderdeliver implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
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
	
	public static String apply(String shopkey, String accountkey) throws Exception {
		String shoployeekey = Textclient.getinstance("unicorn", "shoployee").columnvalues(3).add4create("account", accountkey, 100).add4create("shop", shopkey, 100).add4create("status", "1", 1).create();
		
		long numofshoployees = Textclient.getinstance("unicorn", "shop").key(shopkey).columnamounts(1).add4increment("numofshoployees", 1).increment().get("numofshoployees");
		Indexclient.getinstance("unicorn", shopkey).filters(1).add("shoployees").create(shoployeekey, numofshoployees / 100);
		
		long numofshops = Textclient.getinstance("unicorn", "account").key(accountkey).columnamounts(1).add4increment("numofshops", 1).increment().get("numofshops");
		Indexclient.getinstance("unicorn", accountkey).filters(1).add("shops").create(shoployeekey, numofshops / 100);
		
		return shoployeekey;
	}

}