package com.zdd.bdc.client.biz;

import java.util.UUID;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Bigclient {

	// return [ip][port]
	public static String[] distributebigdata(String namespace, String key) throws Exception {
		String configkey = STATIC.FORMAT_yMd(key);
		String[] servers = STATIC.splitenc(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGDATA).read(configkey));
		return STATIC.splitiport(servers[Math.abs(key.hashCode()) % servers.length]);
	}

	public static String newbigdatakey() {
		return STATIC.FORMAT_KEY(UUID.randomUUID().toString().replaceAll("-", ""));
	}

	// return [ip][port]
	public static String[] distributebigindex(String namespace, Vector<String> filters, String index) {
		return STATIC.splitiport(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX)
				.read(STATIC.distributebigindexserveri(namespace, filters, index,Integer.parseInt(Configclient
						.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
	}

	// return [ip][port]
	public static String[] distributebiguniqueindex(String namespace, String index) {
		return STATIC.splitiport(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX)
				.read(STATIC.distributebigindexserveri(namespace, filters, index,Integer.parseInt(Configclient
						.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
	}

}
