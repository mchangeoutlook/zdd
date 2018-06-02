package com.zdd.bdc.ex;

import java.util.Map;

public interface Theserverprocess {
	public void init(Map<String, String> config);
	public void start(byte[] b) throws Exception;
	public void process(byte[] b) throws Exception;
	public byte[] end() throws Exception;
}
