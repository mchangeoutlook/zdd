package com.zdd.bdc.client.biz;

import java.util.Date;
import java.util.UUID;

import com.zdd.bdc.client.util.STATIC;

public class Bigclient {

	// return [ip][port]
	public static String[] distributebigdata(String namespace, String app, String key) throws Exception {
		Date dateinkey = null;
		try{
			dateinkey = STATIC.yMd_FORMAT(STATIC.FORMAT_yMd(key));
		}catch(Exception e) {
			throw new Exception(STATIC.INVALIDKEY);
		}
		
		String array_date_servernum = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(app+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX);
		String[] date_servernum = STATIC.splitenc(array_date_servernum);
		if (date_servernum==null) {
			throw new Exception(STATIC.INVALIDAPP);
		}
		
		Date targetdate = null;
		long targetservernum = 0;
		for (int i=0;i<date_servernum.length;i++) {
			String[] dateservernum = STATIC.splitenc(date_servernum[i]);
			Date date = STATIC.yMd_FORMAT(dateservernum[0]);
			long servernum = Long.parseLong(dateservernum[1]);
			if (dateinkey.equals(date)) {
				targetdate = date;
				targetservernum = servernum;
				break;
			} else if (dateinkey.before(date)) {
				if (i==0) {
					throw new Exception(STATIC.INVALIDKEY);
				}
				break;
			} else {
				targetdate = date;
				targetservernum = servernum;
			}
		}
		
		return STATIC.splitiport(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA).read(STATIC.splitenc(app,STATIC.yMd_FORMAT(targetdate))+STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE+(Math.abs(key.hashCode()) % targetservernum)));
	}
	
	public static String newbigdatakey() {
		return STATIC.FORMAT_KEY(UUID.randomUUID().toString().replaceAll("-", ""));
	}

	// return [ip][port]
	public static String[] distributebiguniqueindex(String namespace, String servergroups, String index) {
		long servergroupsservernum = Long.parseLong(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
				.read(servergroups+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX));
		return STATIC.splitiport(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
				.read(servergroups+STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE+(Math.abs(index.hashCode())%servergroupsservernum)));
	}

}
