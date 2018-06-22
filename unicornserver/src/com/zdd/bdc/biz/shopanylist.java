package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shopanylist implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		
		Vector<String> shoployeekeys = Indexclient.getinstance("unicorn", bizp.getaccountkey()).filters(1).add("shops").read(0);
		Vector<Map<String, Object>> shops = new Vector<Map<String, Object>>();
		for (String shoployeekey:shoployeekeys) {
			String shopkey = Textclient.getinstance("unicorn", "shoployee").key(shoployeekey).columns(1).add("shop").read().get("shop");
			String shopname = Textclient.getinstance("unicorn", "shop").key(shopkey).columns(1).add("name").read().get("name");
			Map<String, Object> ashop = new Hashtable<String, Object>();
			ashop.put("shopname", shopname);
			ashop.put("shopkey", shopkey);
			
			Vector<String> shoployeekeys2 = Indexclient.getinstance("unicorn", shopkey).filters(1).add("shoployees").read(0);
			Map<String, String> shoployees = new Hashtable<String, String>(shoployeekeys2.size());
			for (String shoployeekey2:shoployeekeys2) {
				Map<String, String> shoployee = Textclient.getinstance("unicorn", "shoployee").key(shoployeekey2).columns(2).add("status").add("account").read();
				String accountkey = shoployee.get("account");
				String login = Textclient.getinstance("unicorn", "account").key(accountkey).columns(1)
						.add("login").read().get("login");
				shoployees.put("shoployeekey", shoployeekey);
				shoployees.put("login", login);
				shoployees.put("status", shoployee.get("status"));
			}	
			ashop.put("shoployees", shoployees);
			shops.add(ashop);
		}
		returnvalue.put("shops", shops);
		return returnvalue;
	}

}