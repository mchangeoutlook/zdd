package com.zdd.bdc.biz;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		Path imagependingfolder = Paths.get(Configclient.getinstance("unicorn", "bigfile").read("filerootfolder")+"pending/");
		Vector<String> pics = new Vector<String>(10000);
		if (Files.exists(imagependingfolder) && Files.isDirectory(imagependingfolder)) {
			Files.walk(imagependingfolder).filter(Files::isRegularFile).forEach(pathfile -> {
				pics.add(pathfile.getParent().getParent().getFileName().toString()+"/"+pathfile.getParent().getFileName().toString()+"/"+pathfile.getFileName().toString());
			});
		}
		returnvalue.put("pics", pics);
		return returnvalue;
	}

}