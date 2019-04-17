package com.tenotenm.yanxin.util;

import java.io.OutputStream;

import com.zdd.bdc.client.ex.Theclientprocess;

public class Downloading implements Theclientprocess {

	private OutputStream os = null;
	public Downloading(OutputStream theos) {
		os =  theos;
	}
	@Override
	public void responses(byte[] arg0) throws Exception {
		os.write(arg0);
		os.flush();
	}

}
