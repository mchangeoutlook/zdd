package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.SS;

public class Filefromserver implements Theserverprocess {
	
	@Override
	public void init(String ip, int port, int bigfilehash, Map<String, Object> additionalserverconfig) {

	}

	@Override
	public byte[] request(byte[] b) throws Exception {
		return null;
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		String path = CS.tostring(param);
		if (path.startsWith("/")) {
			path = path.replaceFirst("/", "");
		}
		return Files.newInputStream(SS.LOCAL_DATAFOLDER.resolve(path));
	}

}
