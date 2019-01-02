package com.zdd.bdc.server.biz;

import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

public class Readroot implements Theclientprocess{

	private String namespace = null;
	private String filter = null;
	private String ip = null;
	private int port = -1;
	private Readleaf readleaf= null;
	
	public Readroot(String thenamespace, String thefilter, String theip, int theport) {
		namespace  = thenamespace;
		filter = thefilter;
		ip = theip;
		port = theport;
		readleaf = new Readleaf(namespace, filter, ip, port);
	}
	@Override
	public void responses(byte[] b) throws Exception {
		String leafile = STATIC.tostring(b);
		if (leafile==null||leafile.trim().isEmpty()) {
			//do nothing
		} else {
			Uniqueindexclient.getinstance(namespace, null).readoneleaf(ip,
					port, filter, leafile, readleaf);
		}
	}

}
