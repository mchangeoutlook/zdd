package com.zdd.bdc.ex;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Theserverprocesstest implements Theserverprocess {
	private String start = null;

	@Override
	public void start(byte[] b) throws Exception {
		start = new String(b,"UTF-8");
	}

	@Override
	public void process(byte[] b) throws Exception {
		Files.write(Paths.get(start), b, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	@Override
	public byte[] end() throws Exception {
		return ("你好="+start).getBytes("UTF-8");
	}
	
}
