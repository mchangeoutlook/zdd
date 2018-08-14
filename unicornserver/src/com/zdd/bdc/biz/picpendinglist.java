package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class picpendinglist implements Ibiz {

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
		Vector<String> pics = new Vector<String>(100);
		Vector<String> pickeys = new Vector<String>(100);
		long pagenum = 0;
		do {
			pickeys = Indexclient.getinstance("unicorn", "ALL").filters(1).add("allreviews")
					.read(pagenum++);
			for (String reviewkey : pickeys) {
				if ("0".equals(Textclient.getinstance("unicorn", "png").key(reviewkey).columns(1).add("status").read()
						.get("status"))) {
					pics.add(reviewkey);
				}
			}
		} while (pics.isEmpty()&&!pickeys.isEmpty());
		returnvalue.put("pics", pics);
		return returnvalue;
	}

}