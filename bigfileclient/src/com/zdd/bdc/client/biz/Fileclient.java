package com.zdd.bdc.biz;

import java.io.InputStream;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.ex.Theclientprocess;
import com.zdd.bdc.util.STATIC;

public class Fileclient {

	private String path = null;

	private Fileclient(String thepath) {
		path = thepath;
	}

	public static Fileclient getinstance(String thepath) {
		return new Fileclient(thepath);
	}

	public void write(String key, InputStream requests) throws Exception {
		try {
			String[] iport = STATIC.splitenc(Bigclient.distributebigdata("pngbigto", key));
			Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes(STATIC.CHARSET_DEFAULT), requests, null);
		} finally {
			requests.close();
		}
	}

	public String read(String key, Theclientprocess cp) throws Exception {
		String[] iport = STATIC.splitenc(Bigclient.distributebigdata("pngbigfrom", key));
		Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes(STATIC.CHARSET_DEFAULT), null, cp);
		return key;
	}

}
