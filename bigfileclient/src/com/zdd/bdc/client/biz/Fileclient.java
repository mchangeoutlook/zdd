package com.zdd.bdc.client.biz;

import java.io.InputStream;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

public class Fileclient {

	private String path = null;

	private Fileclient(String thepath) {
		path = thepath;
		if (!path.endsWith("/")) {
			path +="/";
		}
	}

	public static Fileclient getinstance(String thefolderonserver) {
		return new Fileclient(thefolderonserver);
	}

	public void write(String namespace, String key, InputStream requests) throws Exception {
		try {
			String[] iport = Bigclient.distributebigdata(namespace, key);
			Theclient.request(iport[0], Integer.parseInt(iport[1]), STATIC.tobytes(path+key), requests, null);
		} finally {
			requests.close();
		}
	}

	public String read(String namespace, String key, Theclientprocess cp) throws Exception {
		String[] iport = Bigclient.distributebigdata(namespace, key);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), STATIC.tobytes(path+key), null, cp);
		return key;
	}

	public String delete(String namespace, String key) throws Exception {
		String[] iport = Bigclient.distributebigdata(namespace, key);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), STATIC.tobytes(path+key+STATIC.DELETE_BIGFILE_SUFFIX), null, null);
		return key;
	}

}
