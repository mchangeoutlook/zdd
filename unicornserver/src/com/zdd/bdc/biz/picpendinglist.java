package com.zdd.bdc.biz;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;

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
		Path imagependingfolder = Paths.get(Configclient.getinstance("unicorn", "bigfile").read("filerootfolder")+"pending/");
		if (Files.exists(imagependingfolder) && Files.isDirectory(imagependingfolder)) {
			Files.walk(imagependingfolder).filter(Files::isRegularFile).forEach(pathfile -> {
				System.out.println(pathfile);
			});
		}
		return returnvalue;
	}

}