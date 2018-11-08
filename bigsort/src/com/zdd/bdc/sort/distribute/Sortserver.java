package com.zdd.bdc.sort.distribute;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;

public class Sortserver implements Theserverprocess{

	public static final String sortcheckclasskey = "sortcheckclasskey";
	
	private String ip = null;
	private int port = -1;
	private Sortcheck check = null;
	
	@Override
	public void init(String serverlocalip, int serverlocalport, int bigfilehash, Map<String, Object> additionalserverconfig) throws Exception {
		ip = serverlocalip;
		port = serverlocalport;
		check = (Sortcheck) Class.forName(additionalserverconfig.get(sortcheckclasskey).toString()).getDeclaredConstructor().newInstance();
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
		Path sortingfolder = (Path)params.get(CS.PARAM_KEY_KEY);
		if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			return Objectutil.convert(check.check(sortingfolder));
		} else if (CS.PARAM_ACTION_CREATE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			try {
				String keyamount = params.get(CS.PARAM_DATA_KEY).toString();
				
			}catch(Exception e) {
				System.out.println(new Date()+" ==== error when distributing sort folder ["+sortingfolder+"] on ["+CS.splitiport(ip, String.valueOf(port))+"]");
				e.printStackTrace();
			}
			return null;
		} else {
			return null;
		}
	}


	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
