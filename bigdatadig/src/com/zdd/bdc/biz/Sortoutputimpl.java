package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatautil;
import com.zdd.bdc.sort.local.Sortoutput;

public class Sortoutputimpl implements Sortoutput {
	private String digname = null;
	private String namespace = null;
	// private String table = null;
	// private String col = null;
	//private String filters = null;
	// private String asc_seq = null;
	//private String version = null;
	private int bigfilehash = -1;

	public Sortoutputimpl(int thebigfilehash) {
		bigfilehash = thebigfilehash;
	}

	@Override
	public void init(Path sortingfolder) throws Exception {
		digname = sortingfolder.getParent().getParent().getParent().getParent().getParent().getFileName().toString();
		namespace = sortingfolder.getParent().getParent().getParent().getParent().getFileName().toString();
		//filters = sortingfolder.getParent().getFileName().toString();
		//version = sortingfolder.getFileName().toString();
	}

	@Override
	public void output(long position, String key, long value) throws Exception {
		if (!Configclient.running()) {
			throw new Exception(new Date() + " ==== shutdown this server");
		} else {
			String[] toindex = STATIC.splitenc(
					Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG).read(digname + ".index"));
			if (toindex.length == 1) {
				try{
					Uniqueindexclient.getinstance(namespace, "dig-"+toindex[0]+"-"+position).createunique("pagedkey", key);
				}catch(Exception e) {
					if (e.getMessage()!=null&&e.getMessage().contains(STATIC.DUPLICATE)) {
						Uniqueindexclient.getinstance(namespace, "dig-"+toindex[0]+"-"+position).modifyunique("pagedkey", key);
					}
				}
			} else {
				for (int i = 0; i < toindex.length; i += 2) {
					String t = toindex[i];
					String c = toindex[i + 1];
					try {
						Uniqueindexclient.getinstance(namespace, "dig-"+Filedatautil.read(key, namespace, t, c, bigfilehash)+"-"+position).createunique("pagedkey", key);
					}catch(Exception e) {
						if (e.getMessage()!=null&&e.getMessage().contains(STATIC.DUPLICATE)) {
							Uniqueindexclient.getinstance(namespace, "dig-"+Filedatautil.read(key, namespace, t, c, bigfilehash)+"-"+position).modifyunique("pagedkey", key);
						}
					}
				}
			}
		}
	}

}
