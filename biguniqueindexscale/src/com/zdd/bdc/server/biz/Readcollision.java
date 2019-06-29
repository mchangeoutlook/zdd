package com.zdd.bdc.server.biz;

import java.util.Arrays;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

public class Readcollision implements Theclientprocess{

	private String namespace = null;
	private String filter = null;
	private String servergroups = null;
	private int capacityvalue = -1;
	
	public Readcollision(String thenamespace, String thefilter) {
		namespace  = thenamespace;
		filter = thefilter;
		servergroups = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+filter);
		capacityvalue = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
	}
	@Override
	public void responses(byte[] b) throws Exception {
		String key = STATIC.tostring(Arrays.copyOf(b, capacityvalue));
		String index = STATIC.tostring(b).substring(key.length()).trim();
		try {
			Uniqueindexclient.getinstance(namespace, index).createunique(servergroups, filter, key);
		}catch(Exception e) {
			if (!e.getMessage().contains(STATIC.DUPLICATE)) {
				System.out.println(new Date()+" === fail to process collision value ["+STATIC.tostring(b)+"]");
				throw e;
			}
		}
	}

}
