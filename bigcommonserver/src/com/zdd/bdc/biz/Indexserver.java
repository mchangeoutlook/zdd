package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.util.Map;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Objectutil;

public class Indexserver implements Theserverprocess {

	@SuppressWarnings("unchecked")
	@Override
	public void start(byte[] b) throws Exception {
		
		Map<String, Object> params = (Map<String, Object>)Objectutil.convert(b);
		if ("create".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Map<String, String> cvs = (Map<String, String>)params.get("cvs");
			Map<String, Integer> cvmaxs = (Map<String, Integer>)params.get("cvmaxs");

		}
	}

	@Override
	public void process(byte[] b) throws Exception {
		
	}

	@Override
	public byte[] end() throws Exception {
		return null;
	}
	
	private static Path target() {
		
	}

}
