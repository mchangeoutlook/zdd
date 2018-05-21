package com.zdd.bdc.biz;

import com.zdd.bdc.ex.Theserverprocess;

public class Textserver implements Theserverprocess {

	byte[] start = null;
	@Override
	public void start(byte[] b) throws Exception {
		start = b;
	}

	@Override
	public void process(byte[] b) throws Exception {
		
	}

	@Override
	public byte[] end() throws Exception {
		
		return start;
	}

}
