package com.zdd.bdc.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		String imagerootfolder = Configclient.getinstance("unicorn", "bigfile").read("filerootfolder");
		if (!bizp.getfilenames().isEmpty()) {
			for (String filename:bizp.getfilenames()) {
				String targetfiletemp = UUID.randomUUID().toString().replaceAll("-", "");
				String targetfilepending = targetfiletemp;
				String relativepath = targetfiletemp;
				if (bizp.getext("resourceid").equals(bizp.getaccountkey())) {
					relativepath = "approved/account/"+relativepath;
					targetfilepending = imagerootfolder+"pending/"+UUID.randomUUID().toString().replaceAll("-", "")+"/account/"+targetfilepending;
					targetfiletemp = imagerootfolder+"approved/account/"+targetfiletemp;
				} else {
					relativepath = "approved/"+bizp.getext("resourceid")+"/"+relativepath;
					targetfilepending = imagerootfolder+"pending/"+UUID.randomUUID().toString().replaceAll("-", "")+"/"+bizp.getext("resourceid")+"/"+targetfilepending;
					targetfiletemp = imagerootfolder+"approved/"+bizp.getext("resourceid")+"/"+targetfiletemp;
				}
				Fileclient.copyto(Configclient.getinstance("unicorn", "bigfile").read("fileserverip"), 
						Integer.parseInt(Configclient.getinstance("core", "core").read("fileserverport")), 
						targetfilepending, bizp.getfile(filename));
				InputStream is = Files.newInputStream(Paths.get("/Users/mido/Downloads/test.jpeg"));
				try {
					Fileclient.copyto(Configclient.getinstance("unicorn", "bigfile").read("fileserverip"), 
							Integer.parseInt(Configclient.getinstance("core", "core").read("fileserverport")), 
									targetfiletemp, is);
					returnvalue.put(filename, relativepath);
				}finally{
					if (is!=null) {
						is.close();
					}
				}
			}
		}
		
		return returnvalue;
	}

}