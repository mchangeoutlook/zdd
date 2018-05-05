package com.zdd.bdc.util;

public interface Theserverprocess {
	public void start(byte[] b) throws Exception;
	public void process(byte[] b) throws Exception;
	public byte[] end() throws Exception;
}
