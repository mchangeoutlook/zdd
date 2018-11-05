package com.zdd.bdc.sort.distribute;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.sort.local.Sortcontrol;
import com.zdd.bdc.sort.local.Sortfactory;

public class Sortserver implements Theserverprocess{

	private Integer returnvalue = -1;
	
	private String ip = null;
	private int port = -1;
	
	@Override
	public void init(String serverlocalip, int serverlocalport, int bigfilehash) {
		// TODO Auto-generated method stub
		ip = serverlocalip;
		port = serverlocalport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		Path sortingfolder = (Path)params.get(CS.PARAM_KEY_KEY);
		if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			returnvalue = Sortfactory.getss(sortingfolder).status(sortingfolder);
		} else if (CS.PARAM_ACTION_CREATE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			try {
				String keyamount = params.get(CS.PARAM_DATA_KEY).toString();
				
			}catch(Exception e) {
				Sortcontrol.to(sortingfolder,Sortcontrol.TERMINATE);
				returnvalue = Sortcontrol.to(sortingfolder);
				System.out.println(new Date()+" ==== error when distributing sort folder ["+sortingfolder+"] on ["+CS.splitiport(ip, String.valueOf(port))+"]");
				e.printStackTrace();
			}
		} else {
			//do nothing
		}
	}

	@Override
	public void requests(byte[] b) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] response() throws Exception {
		return Objectutil.convert(returnvalue);
	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
