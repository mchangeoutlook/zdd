package com.zdd.bdc.biz;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.ex.Theserverprocess;

public class Fileserver implements Theserverprocess {
	
	private Path targetpath;
	
	@Override
	public void start(byte[] b) throws Exception {
		targetpath = Paths.get(new String(b,"UTF-8"));
	}

	@Override
	public void process(byte[] b) throws Exception {
		if (!Files.exists(targetpath)&&targetpath.getParent()!=null&&!Files.exists(targetpath.getParent())) {
			Files.createDirectories(targetpath.getParent());
		}
		Files.write(targetpath, b, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	@Override
	public byte[] end() throws Exception {
		return null;
	}

}
