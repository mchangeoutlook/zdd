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
									.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter)));
					for (int j = 0; j < uniqueservers; j++) {
						final int server = j;
						new Thread(new Runnable() {

							@Override
							public void run() {
								String[] folderipport = STATIC.splitenc(Configclient
										.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(Configclient
												.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX).read(filter) + server));
								System.out.println(new Date() + " ==== starting [" + namespace + "][" + filter + "] on ["+folderipport[1]+"]["+folderipport[2]+"]");
								try {
									Uniqueindexclient.getinstance(namespace, null).readallroots(folderipport[1],
											Integer.parseInt(folderipport[2]), filter, new Readroot(namespace, filter,folderipport[1], Integer.parseInt(folderipport[2]) ));
									System.out.println(new Date() + " ==== done [" + namespace + "][" + filter + "] on ["+folderipport[1]+"]["+folderipport[2]+"]");
								} catch (Exception e) {
									System.out.println(new Date() + " ==== terminated [" + namespace + "][" + filter + "] on ["+folderipport[1]+"]["+folderipport[2]+"]");
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
