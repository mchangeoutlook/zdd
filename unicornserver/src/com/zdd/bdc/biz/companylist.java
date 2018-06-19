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
		
		Vector<String> employeekeys = Indexclient.getinstance("unicorn", bizp.getaccountkey()).filters(1).add("companies").read(0);
		Vector<Map<String, Object>> companies = new Vector<Map<String, Object>>();
		for (String employeekey:employeekeys) {
			String companykey = Textclient.getinstance("unicorn", "employee").key(employeekey).columns(1).add("company").read().get("company");
			String companyname = Textclient.getinstance("unicorn", "company").key(companykey).columns(1).add("name").read().get("name");
			Map<String, Object> acompany = new Hashtable<String, Object>();
			acompany.put("companyname", companyname);
			acompany.put("companykey", companykey);
			
			Vector<String> comemployeekeys = Indexclient.getinstance("unicorn", companykey).filters(1).add("employees").read(0);
			Map<String, String> comemployees = new Hashtable<String, String>(comemployeekeys.size());
			for (String comemployeekey:comemployeekeys) {
				Map<String, String> employee = Textclient.getinstance("unicorn", "employee").key(comemployeekey).columns(2).add("status").add("account").read();
				String accountkey = employee.get("account");
				String login = Textclient.getinstance("unicorn", "account").key(accountkey).columns(1)
						.add("login").read().get("login");
				comemployees.put("employeekey", comemployeekey);
				comemployees.put("login", login);
				comemployees.put("status", employee.get("status"));
			}	
			acompany.put("employees", comemployees);
			companies.add(acompany);
		}
		returnvalue.put("companies", companies);
		return returnvalue;
	}

}