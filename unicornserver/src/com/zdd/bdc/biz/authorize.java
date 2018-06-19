package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class authorize implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetaccountkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetactioncode", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("targetresourceid", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("targetresourceid");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		auth(bizp.getext("targetaccountkey"), bizp.getext("targetresourceid"), bizp.getext("targetactioncode"));
		return returnvalue;
	}
	
	public static void auth(String accountkey, String resourceid, String actioncode) throws Exception {
		String authkey = Textclient.getinstance("unicorn", "auth").columnvalues(1).add4create("code", actioncode, 25).create();
		Indexclient.getinstance("unicorn", accountkey+Ibiz.SPLITTER+resourceid).filters(1).add(actioncode).createunique(authkey);
	}
	
	public static boolean authcheck(String accountkey, String resourceid, String actioncode) throws Exception {
		String authcode = Textclient.getinstance("unicorn", "auth").key(Indexclient.getinstance("unicorn", accountkey+Ibiz.SPLITTER+resourceid).filters(1).add(actioncode).readunique()).columns(1).add("code").read().get("code");
		if (actioncode.equals(authcode)) {
			return true;
		}
		return false;
	}
	public static void unauth(String accountkey, String resourceid, String actioncode) throws Exception {
		String authkey = Indexclient.getinstance("unicorn", accountkey+Ibiz.SPLITTER+resourceid).filters(1).add(actioncode).readunique();
		Textclient.getinstance("unicorn", "auth").key(authkey).columnvalues(1).add4modify("code", "").modify();
	}
	

}