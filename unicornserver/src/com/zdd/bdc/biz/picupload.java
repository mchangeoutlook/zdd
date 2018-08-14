package com.zdd.bdc.biz;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import com.zdd.bdc.util.Bizparams;
import com.zdd.bdc.util.Ibiz;

public class picupload implements Ibiz {
	
	@Override
	public Map<String, String> validrules() {
		Map<String, String> returnvalue = new Hashtable<String, String>();
		returnvalue.put("loginkey", Ibiz.VALIDRULE_NOTEMPTY);
		returnvalue.put("resourceid", Ibiz.VALIDRULE_NOTEMPTY);
		return returnvalue;
	}

	@Override
	public String auth(Bizparams bizp) throws Exception {
		return bizp.getext("resourceid");
	}

	@Override
	public Map<String, Object> process(Bizparams bizp) throws Exception {
		Map<String, Object> returnvalue = new Hashtable<String, Object>();
		String abigkey = Indexclient.getinstance("unicorn", "abig").readunique();
		if (!bizp.getfilenames().isEmpty()) {
			for (String filename:bizp.getfilenames()) {
				String path = UUID.randomUUID().toString().replaceAll("-", "");
				if (bizp.getext("resourceid").equals(bizp.getaccountkey())) {
					path = "account/"+path;
				} else {
					path = bizp.getext("resourceid")+"/"+path;
				}
				String type = "head";
				if (filename.contains("content")) {
					type = "content";
				}
				String key = Textclient.getinstance("unicorn", "png").columnvalues(3).add4create("status", "0", 1).add4create("path", path, 73).add4create("type", type, 7).create();
				Fileclient.getinstance(path).write(key, bizp.getfile(filename));
				
				long numofreviews = Textclient.getinstance("unicorn", "abig").key(abigkey).columnamounts(1).add4increment("numofreviews", 1).increment().get("numofreviews");
				Indexclient.getinstance("unicorn", "ALL").filters(1).add("allreviews").create(key, numofreviews / 100);
		
				returnvalue.put(filename, key);
			}
		}
		
		return returnvalue;
	}

}