package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Pagedindexclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatautil;
import com.zdd.bdc.sort.local.Sortoutput;

public class Sortoutputimpl implements Sortoutput {
	private String digname = null;
	private String namespace = null;
	// private String table = null;
	// private String col = null;
	private String filters = null;
	// private String asc_seq = null;
	private String version = null;
	private int bigfilehash = -1;

	public Sortoutputimpl(int thebigfilehash) {
		bigfilehash = thebigfilehash;
	}

	@Override
	public void init(Path sortingfolder) throws Exception {
		digname = sortingfolder.getParent().getParent().getParent().getParent().getParent().getFileName().toString();
		namespace = sortingfolder.getParent().getParent().getParent().getParent().getFileName().toString();
		filters = sortingfolder.getParent().getFileName().toString();
		version = sortingfolder.getFileName().toString();
	}

	@Override
	public void output(long position, String key, long value) throws Exception {
		if (!Configclient.running) {
			throw new Exception(new Date() + " ==== shutdown this server");
		} else {
			String[] toindex = STATIC.splitenc(
					Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_DIG).read(digname + ".index"));
			if (toindex.length == 1) {
				index(namespace, toindex[0], key, version);
			} else {
				for (int i = 0; i < toindex.length; i += 2) {
					String t = toindex[i];
					String c = toindex[i + 1];
					index(namespace, Filedatautil.read(key, namespace, t, c, bigfilehash), key, version);
				}
			}
		}
	}

	private Long index(String namespace, String index, String key, String version) throws Exception {
		Pagedindexclient ic = Pagedindexclient.getinstance(namespace, index);
		String[] fs = STATIC.splitenc(filters);
		for (String filter : fs) {
			ic.addfilter(filter);
		}
		return ic.create(key, version);
	}

}
