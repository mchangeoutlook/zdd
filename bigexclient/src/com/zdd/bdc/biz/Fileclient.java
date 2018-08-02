package com.zdd.bdc.biz;

import java.io.InputStream;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.ex.Theclientprocess;

public class Fileclient {
	public static void copyto(String ip, int port, String targetpath, InputStream ins) throws Exception {
		Theclient.request(ip, port, targetpath.getBytes("UTF-8"), ins, null);
	}
	public static void copyfrom(String ip, int port, String targetpath, Theclientprocess cp) throws Exception {
		Theclient.request(ip, port, targetpath.getBytes("UTF-8"), null, cp);
	}
}
