package com.zdd.bdc.util;

import java.io.OutputStream;

import com.zdd.bdc.ex.Theclientprocess;

public class Downloadpng implements Theclientprocess {

	private OutputStream os = null;
	public Downloadpng(OutputStream theos) {
		os =  theos;
	}
	@Override
	public void responses(byte[] arg0) throws Exception {
		os.write(arg0);
		os.flush();
	}

}
