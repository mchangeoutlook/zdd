package com.tenotenm.yanxin.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.ex.Theclientprocess;

public class Downloading implements Theclientprocess {

	public String localtempfile = null;
	
	public Downloading() {
		localtempfile =  Bigclient.newbigdatakey();
	}
	@Override
	public void responses(byte[] arg0) throws Exception {
		Files.write(Paths.get(localtempfile), arg0, StandardOpenOption.CREATE,StandardOpenOption.APPEND, StandardOpenOption.SYNC);
	}
	
}
