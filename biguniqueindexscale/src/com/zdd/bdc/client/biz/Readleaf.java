package com.zdd.bdc.client.biz;

import java.util.Arrays;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.ex.Theclientprocess;
import com.zdd.bdc.client.util.STATIC;

public class Readleaf implements Theclientprocess{

	private String namespace = null;
	private String filter = null;
	private String ip = null;
	private int port = -1;
	private String servergroups = null;
	private int capacityvalue = -1;
	private Readcollision readcollision = null;
	
	public Readleaf(String thenamespace, String thefilter, String theip, int theport) {
		namespace  = thenamespace;
		filter = thefilter;
		ip = theip;
		port = theport;
		servergroups = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+filter);
		capacityvalue = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
		readcollision =new Readcollision(namespace, filter);
	}
	@Override
	public void responses(byte[] b) throws Exception {
		String leafvalue = STATIC.tostring(b).trim();
		if (leafvalue==null||leafvalue.trim().isEmpty()) {
			//do nothing
		} else {
			if (leafvalue.length()==32) {
				Uniqueindexclient.getinstance(namespace, null).readonecollision(ip,
						port, filter, leafvalue, readcollision);
			} else {
				String key = STATIC.tostring(Arrays.copyOf(b, capacityvalue));
				String index = leafvalue.substring(key.length()).trim();
				try {
					Uniqueindexclient.getinstance(namespace, index).createunique(servergroups, filter, key);
				}catch(Exception e) {
					if (!e.getMessage().contains(STATIC.DUPLICATE)) {
						System.out.println(new Date()+" === fail to process leaf value ["+leafvalue+"]");
						throw e;
					}
				}
			}
		}
	}

}
