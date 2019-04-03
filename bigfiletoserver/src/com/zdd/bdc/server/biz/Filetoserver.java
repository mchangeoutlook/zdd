package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;

public class Filetoserver implements Theserverprocess {
	
	@Override
	public void init(String ip, int port, int bigfilehash, Map<String, Object> additionalserverconfig) {

	}

	@Override
	public byte[] request(byte[] b) throws Exception {
		String path = STATIC.tostring(b);
		if (path.endsWith(STATIC.DELETE_BIGFILE_SUFFIX)) {
			path = path.substring(0, path.length()-STATIC.DELETE_BIGFILE_SUFFIX.length());
			Path targetpath = path.startsWith("/")?STATIC.LOCAL_DATAFOLDER.resolve(path.replaceFirst("/", "")):STATIC.LOCAL_DATAFOLDER.resolve(path);
			targetpath.toFile().delete();
		}
		return null;
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		return new Inputprocess() {
			private Path targetpath = STATIC.tostring(param).startsWith("/")?STATIC.LOCAL_DATAFOLDER.resolve(STATIC.tostring(param).replaceFirst("/", "")):STATIC.LOCAL_DATAFOLDER.resolve(STATIC.tostring(param));
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

	@Override
	public int requestoutputbytes(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return 10240;
	}

}
