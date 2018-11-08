package com.zdd.bdc.server.ex;

import java.io.InputStream;
import java.util.Map;

public interface Theserverprocess {
	public void init(String serverlocalip, int serverlocalport, int bigfilehash, Map<String, Object> additionalserverconfig) throws Exception;
	public byte[] request(byte[] param) throws Exception;
	public Inputprocess requestinput(byte[] param) throws Exception;
	public InputStream requestoutput(byte[] param) throws Exception;
}
