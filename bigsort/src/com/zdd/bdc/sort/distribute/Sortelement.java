package com.zdd.bdc.sort.distribute;

public class Sortelement {
	private String ip = null;
	private int port = -1;
	private String key = null;
	private long amount  = -1;
	public Sortelement(String theip, int theport, String thekey, long theamount) {
		ip = theip;
		port = theport;
		key = thekey;
		amount = theamount;
	}
	public String ip() {
		return ip;
	}
	public int port() {
		return port;
	}
	public String key() {
		return key;
	}
	public long amount() {
		return amount;
	}
	
}
