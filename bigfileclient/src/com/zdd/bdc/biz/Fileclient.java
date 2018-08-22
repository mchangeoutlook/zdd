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
			String[] iport = Bigclient.distributebigdata("pngbigto", key).split(STATIC.SPLIT_IP_PORT);
			Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes("UTF-8"), requests, null);
		} finally {
			requests.close();
		}
	}

	public String read(String key, Theclientprocess cp) throws Exception {
		String[] iport = Bigclient.distributebigdata("pngbigfrom", key).split(STATIC.SPLIT_IP_PORT);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes("UTF-8"), null, cp);
		return key;
	}

}
