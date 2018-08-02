package com.zdd.bdc.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.zdd.bdc.ex.Theserverprocess;

public class Filetoserver implements Theserverprocess {
	
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
		if (!Files.exists(targetpath)&&targetpath.getParent()!=null&&!Files.exists(targetpath.getParent())) {
			Files.createDirectories(targetpath.getParent());
		}
		Files.write(targetpath, b, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	@Override
	public byte[] response() throws Exception {
		return "done".getBytes();
	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
