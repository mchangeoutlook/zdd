package com.zdd.bdc.client.main;

import java.util.Date;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Readroot;
import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.util.STATIC;

public class Startuniqueindexscale {
	public static void main(String[] s) throws Exception {
		try {
			String namespace = s[0];
			for (int i = 1; i < s.length; i++) {
				String filter = s[i];
				String scale = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)
						.read(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX + filter);
				if (scale == null || scale.trim().isEmpty() || scale.equals(
						Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter))) {
					System.out.println(new Date() + " ==== ignore [" + namespace + "][" + filter + "] due to scale servergroups ["+scale+"] same as current ["+Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter)+"]");
					continue;
				} else {
					int uniqueservers = Integer.parseInt(
							Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(Configclient
									.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter)+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX));
					for (int j = 0; j < uniqueservers; j++) {
						final int server = j;
						new Thread(new Runnable() {

							@Override
							public void run() {
								String[] ipport = STATIC.splitiport(Configclient
										.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(Configclient
												.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter) +STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE+ server));
								System.out.println(new Date() + " ==== starting [" + namespace + "][" + filter + "] on ["+ipport[0]+"]["+ipport[1]+"]");
								try {
									Uniqueindexclient.getinstance(namespace, null).readallroots(ipport[0],
											Integer.parseInt(ipport[1]), filter, new Readroot(namespace, filter,ipport[0], Integer.parseInt(ipport[1]) ));
									System.out.println(new Date() + " ==== done [" + namespace + "][" + filter + "] on ["+ipport[0]+"]["+ipport[1]+"]");
								} catch (Exception e) {
									System.out.println(new Date() + " ==== terminated [" + namespace + "][" + filter + "] on ["+ipport[0]+"]["+ipport[1]+"]");
									e.printStackTrace();
								}
							}
							
						}).start();
					}
				}
			}
		} finally {
			Configclient.shutdownifpending.append(STATIC.REMOTE_CONFIGVAL_PENDING);
		}
	}

}
