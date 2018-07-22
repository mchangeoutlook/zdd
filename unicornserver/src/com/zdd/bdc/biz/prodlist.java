package com.zdd.bdc.biz;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class prodlist implements Ibiz {

	@Override
	public Map<String, String> validrules() {
		return null;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		Vector<String> prodkeys = Indexclient.getinstance("unicorn", "ALL").filters(1).add("allprods").read(0);
		List<Map<String, String>> prods = new ArrayList<Map<String, String>>();
		for (String prodkey:prodkeys) {
			Map<String, String> prod = Textclient.getinstance("unicorn", "prod").key(prodkey).columns(5)
			.add("status").add("name").add("rp").add("headimg").add("contentimgs").read();
			prod.put("prodkey", prodkey);
			prods.add(prod);
		}
		returnvalue.put("prods", prods);
		return returnvalue;
	}
	
}