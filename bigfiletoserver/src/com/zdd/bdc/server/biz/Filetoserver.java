package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.SS;

public class Filetoserver implements Theserverprocess {
	
	@Override
	public void init(String ip, int port, int bigfilehash, Map<String, Object> additionalserverconfig) {

	}

	@Override
	public byte[] request(byte[] b) throws Exception {
		return null;
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		return new Inputprocess() {
			private Path targetpath = CS.tostring(param).startsWith("/")?SS.LOCAL_DATAFOLDER.resolve(CS.tostring(param).replaceFirst("/", "")):SS.LOCAL_DATAFOLDER.resolve(CS.tostring(param));
			@Override
			public void process(byte[] b) throws Exception {
				if (!Files.exists(targetpath)&&targetpath.getParent()!=null&&!Files.exists(targetpath.getParent())) {
					Files.createDirectories(targetpath.getParent());
				}
				Files.write(targetpath, b, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
			
		};
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
