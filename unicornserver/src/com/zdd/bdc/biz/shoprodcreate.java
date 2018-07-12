package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shoprodcreate implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopkey", Ibiz.VALIDRULE_NOTEMPTY);
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
		String prodkey = Textclient.getinstance("unicorn", "prod").columnvalues(7)
				.add4create("loginkey", bizp.getloginkey(), 100).add4create("admin", bizp.getaccountkey(), 100)
				.add4create("name", bizp.getext("prodname"), 100).add4create("rp", bizp.getext("prodrp"), 10).add4create("category", bizp.getext("categorykey"), 100)
				.add4create("headimg", bizp.getext("headimg"), 100).add4create("contentimgs", bizp.getext("contentimgs"), 1000).create();
		Indexclient.getinstance("unicorn", bizp.getext("prodname")).filters(1).add("prod").createunique(prodkey);
		
		long numofshoprods = Textclient.getinstance("unicorn", "shop").key(bizp.getext("shopkey")).columnamounts(1).add4increment("numofshoprods", 1).increment().get("numofshoprods");
		Indexclient.getinstance("unicorn", bizp.getext("shopkey")).filters(1).add("shoprods").create(prodkey, numofshoprods / 100);
		
		String abigkey = Indexclient.getinstance("unicorn", "abig").readunique();
		long numofprods = Textclient.getinstance("unicorn", "abig").key(abigkey).columnamounts(1).add4increment("numofpros", 1).increment().get("numofpros");
		Indexclient.getinstance("unicorn", "ALL").filters(1).add("allprods").create(prodkey, numofprods / 100);
		
		return returnvalue;
	}

}