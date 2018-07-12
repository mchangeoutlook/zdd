package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;
import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class shopcreate implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("shopname", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		if (!Indexclient.getinstance("unicorn", bizp.getext("shopname")).filters(1).add("shop").readunique().isEmpty()){
			throw new Exception("duplicate");
		}
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		String shopkey = Textclient.getinstance("unicorn", "shop").columnvalues(4)
				.add4create("loginkey", bizp.getloginkey(), 100).add4create("admin", bizp.getaccountkey(), 100)
				.add4create("name", bizp.getext("shopname"), 100).create();
		Indexclient.getinstance("unicorn", bizp.getext("shopname")).filters(1).add("shop").createunique(shopkey);
		
		String shoployeekey = shoployeeapply.apply(shopkey, bizp.getaccountkey());
		shoployeeapprove.approve(shoployeekey);
		
		authorize.authassign(bizp.getaccountkey(), shopkey, "authorize");
		authorize.authassign(bizp.getaccountkey(), shopkey, "picupload");
		authorize.authassign(bizp.getaccountkey(), shopkey, "shoprodcreate");
		authorize.authassign(bizp.getaccountkey(), shopkey, "shoprodmodify");
		authorize.authassign(bizp.getaccountkey(), shopkey, "shoprodpending");
		authorize.authassign(bizp.getaccountkey(), shopkey, "shoprodapprove");
		
		return returnvalue;
	}

}