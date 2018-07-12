package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class abiginit implements Ibiz {

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
		if (!Indexclient.getinstance("unicorn", "abig").readunique().isEmpty()) {
			throw new Exception("duplicate");
		}
		String abigkey = Textclient.getinstance("unicorn", "abig").columnvalues(2)
				.add4create("loginkey", bizp.getloginkey(), 100).add4create("admin", bizp.getaccountkey(), 100)
				.create();
		Indexclient.getinstance("unicorn", "abig").createunique(abigkey);

		return returnvalue;
	}

}