package com.zdd.bdc.ex;

import java.io.InputStream;
import java.util.Map;

public interface Theserverprocess {
	public void init(Map<String, String> config);
	public void request(byte[] b) throws Exception;
	public void requests(byte[] b) throws Exception;
	public byte[] response() throws Exception;
	public InputStream responses() throws Exception;
}
