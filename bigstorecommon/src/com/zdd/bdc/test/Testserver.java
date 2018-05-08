package com.zdd.bdc.test;

import com.zdd.bdc.biz.Fileserver;
import com.zdd.bdc.biz.Textserver;
import com.zdd.bdc.ex.Theserver;

public class Testserver {
	public static void main(String[] s) throws Exception {
		Theserver.startblocking(9999, Textserver.class);
	}
}
