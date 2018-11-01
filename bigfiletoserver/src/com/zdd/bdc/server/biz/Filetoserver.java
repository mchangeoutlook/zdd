package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.SS;

public class Filetoserver implements Theserverprocess {
	
	private Path targetpath;
	
	@Override
	public void init(String ip, int port, int bigfilehash) {

	}

	@Override
	public void request(byte[] b) throws Exception {
		String path = CS.tostring(b);
		if (path.startsWith("/")) {
			path = path.replaceFirst("/", "");
		}
		targetpath = SS.LOCAL_DATAFOLDER.resolve(path);
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
