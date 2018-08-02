package com.zdd.bdc.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.zdd.bdc.ex.Theserverprocess;

public class Filefromserver implements Theserverprocess {
	
	private Path targetpath;
	
	@Override
	public void init(Map<String, String> config) {

	}

	@Override
	public void request(byte[] b) throws Exception {
		targetpath = Paths.get(new String(b,"UTF-8"));
	}

	@Override
	public void requests(byte[] b) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] response() throws Exception {
		// TODO Auto-generated method stub
		return "done".getBytes();
	}

	@Override
	public InputStream responses() throws Exception {
		return Files.newInputStream(targetpath);
	}

}
