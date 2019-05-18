package com.tenotenm.yanxin.util;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.zdd.bdc.client.ex.Theclientprocess;

public class Downloading implements Theclientprocess {

	private HttpServletResponse res = null;
	private OutputStream os = null;
	public Downloading(HttpServletResponse theres) {
		res =  theres;
	}
	@Override
	public void responses(byte[] arg0) throws Exception {
		if (os==null) {
			res.setContentType("image/*");
			os = res.getOutputStream();
		}
		os.write(arg0);
		os.flush();
	}

}
