package com.xinzyan.yanxin.stat.util;

import java.util.Arrays;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

public class Readcollision implements Theclientprocess{

	private String namespace = null;
	private String filter = null;
	private String yesterday = null;
	private String statkey = null;
	private String servergroups = null;
	private int capacityvalue = -1;
	
	public Readcollision(String thenamespace, String thefilter, String yesterday, String statkey) {
		namespace  = thenamespace;
		filter = thefilter;
		this.yesterday = yesterday;
		this.statkey = statkey;
		servergroups = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+filter);
		capacityvalue = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
	}
	@Override
	public void responses(byte[] b) throws Exception {
		String key = STATIC.tostring(Arrays.copyOf(b, capacityvalue));
		//String index = STATIC.tostring(b).substring(key.length()).trim();
		Stat.statyxaccount(yesterday, statkey, key);
	}

}
