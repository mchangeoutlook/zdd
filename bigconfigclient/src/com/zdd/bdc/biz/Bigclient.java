package com.zdd.bdc.biz;

import java.util.UUID;

import com.zdd.bdc.util.STATIC;

public class Bigclient {

	//return ip:port
	public static String distributebigdata(String namespace, String key) {
		String configkey = STATIC.FORMAT_yMd(key);
		String[] servers = STATIC.split(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(configkey));
		return servers[Math.abs(key.hashCode())%servers.length];
	}

	public static String newbigdatakey() {
		return STATIC.FORMAT_KEY(UUID.randomUUID().toString().replaceAll("-", ""));
	}
	
	//return ip:port
	public static String distributebigindex(String namespace, long pagenum, String index) {
		return Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGINDEX)
				.read(String.valueOf(Math.abs((pagenum + index).hashCode())
						% Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS))));
	}
	
	public static String distributebigindexserveri(String namespace, long pagenum, String index) {
		return String.valueOf(Math.abs((pagenum + index).hashCode())
				% Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)));
	}
}
