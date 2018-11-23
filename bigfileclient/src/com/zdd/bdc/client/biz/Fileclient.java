package com.zdd.bdc.client.biz;

import java.io.InputStream;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

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
			String[] iport = Bigclient.distributebigdata("pngbigto", key);
			Theclient.request(iport[0], Integer.parseInt(iport[1]), STATIC.tobytes(path), requests, null);
		} finally {
			requests.close();
		}
	}

	public String read(String key, Theclientprocess cp) throws Exception {
		String[] iport = Bigclient.distributebigdata("pngbigfrom", key);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), STATIC.tobytes(path), null, cp);
		return key;
	}

}
