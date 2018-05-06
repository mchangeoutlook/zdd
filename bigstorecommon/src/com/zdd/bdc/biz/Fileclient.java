package com.zdd.bdc.biz;

import java.io.InputStream;

import com.zdd.bdc.ex.Theclient;

public class Fileclient {
	public static void copytoremote(String ip, int port, String targetpath, InputStream ins) throws Exception {
		Theclient.request(ip, port, targetpath.getBytes("UTF-8"), ins);
	}
}
