package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class companylist implements Ibiz {

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
		
		Vector<String> comployeekeys = Indexclient.getinstance("unicorn", bizp.getaccountkey()).filters(1).add("companies").read(0);
		Vector<Map<String, Object>> companies = new Vector<Map<String, Object>>();
		for (String comployeekey:comployeekeys) {
			String companykey = Textclient.getinstance("unicorn", "comployee").key(comployeekey).columns(1).add("company").read().get("company");
			String companyname = Textclient.getinstance("unicorn", "company").key(companykey).columns(1).add("name").read().get("name");
			Map<String, Object> acompany = new Hashtable<String, Object>();
			acompany.put("companyname", companyname);
			acompany.put("companykey", companykey);
			
			Vector<String> comployeekeys2 = Indexclient.getinstance("unicorn", companykey).filters(1).add("comployees").read(0);
			Map<String, String> comployees = new Hashtable<String, String>(comployeekeys2.size());
			for (String comployeekey2:comployeekeys2) {
				Map<String, String> comployee = Textclient.getinstance("unicorn", "comployee").key(comployeekey2).columns(2).add("status").add("account").read();
				String accountkey = comployee.get("account");
				String login = Textclient.getinstance("unicorn", "account").key(accountkey).columns(1)
						.add("login").read().get("login");
				comployees.put("comployeekey", comployeekey2);
				comployees.put("login", login);
				comployees.put("status", comployee.get("status"));
			}	
			acompany.put("comployees", comployees);
			companies.add(acompany);
		}
		returnvalue.put("companies", companies);
		return returnvalue;
	}

}