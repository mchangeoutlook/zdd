package com.zdd.bdc.client.biz;

import java.util.UUID;

import com.zdd.bdc.client.util.CS;

public class Bigclient {

	//return [ip][port]
	public static String[] distributebigdata(String namespace, String key) throws Exception {
		String configkey = CS.FORMAT_yMd(key);
		String[] servers = CS.splitenc(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_BIGDATA).read(configkey));
		return CS.splitiport(servers[Math.abs(key.hashCode())%servers.length]);
	}

	public static String newbigdatakey() {
		return CS.FORMAT_KEY(UUID.randomUUID().toString().replaceAll("-", ""));
	}
	
	//return [ip][port]
	public static String[] distributebigindex(String namespace, Long pagenum, String index) {
		return CS.splitiport(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_BIGINDEX)
				.read(String.valueOf(Math.abs((pagenum + index).hashCode())
						% Integer.parseInt(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_BIGINDEX).read(CS.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
	}
	
	public static String distributebigindexserveri(String namespace, Long pagenum, String index) {
		return String.valueOf(Math.abs((pagenum + index).hashCode())
				% Integer.parseInt(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_BIGINDEX).read(CS.REMOTE_CONFIGKEY_MAXINDEXSERVERS)));
	}
}
