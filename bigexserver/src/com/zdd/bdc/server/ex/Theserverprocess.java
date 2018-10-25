package com.zdd.bdc.server.ex;

import java.io.InputStream;

public interface Theserverprocess {
	public void init(int bigfilehash);
	public void request(byte[] b) throws Exception;
	public void requests(byte[] b) throws Exception;
	public byte[] response() throws Exception;
	public InputStream responses() throws Exception;
}
