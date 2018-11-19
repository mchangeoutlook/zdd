package com.zdd.bdc.biz;

import java.nio.file.Path;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Indexclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.SS;
import com.zdd.bdc.sort.local.Sortoutput;

public class Sortoutputimpl implements Sortoutput{
	private String digname = null;
	private String namespace = null;
	//private String table = null;
	//private String col = null;
	private String filters = null;
	private String asc_seq = null;
	private String version = null;
	private int bigfilehash = -1;
	
	public Sortoutputimpl(int thebigfilehash) {
		bigfilehash = thebigfilehash;
	}
	
	@Override
	public void init(Path sortingfolder) throws Exception {
		digname = sortingfolder.getParent().getParent().getParent().getParent().getParent().getParent().getFileName().toString();		
		namespace = sortingfolder.getParent().getParent().getParent().getParent().getParent().getFileName().toString();		
		//table = sortingfolder.getParent().getParent().getParent().getParent().getFileName().toString();		
		//col = sortingfolder.getParent().getParent().getParent().getFileName().toString();		
		filters = sortingfolder.getParent().getParent().getFileName().toString();		
		asc_seq = sortingfolder.getParent().getFileName().toString();
		version = sortingfolder.getFileName().toString();
	}

	@Override
	public void output(long position, String key, long value) throws Exception {
		String[] toindex = CS.splitenc(Configclient.getinstance(namespace, SS.REMOTE_CONFIG_DIG)
				.read(digname + ".index"));
		if (toindex.length==1) {
			index(namespace, position, toindex[0], key, version);
		} else {
			String ns = toindex[0];
			for (int i = 1; i< toindex.length;i+=2) {
				String t = toindex[i];
				String c = toindex[i+1];	
				index(ns, position, Filekvutil.dataread(key, ns, t, c, bigfilehash), key, version);
			}
		}
	}
	
	private void index(String namespace, long position, String index, String key, String version) throws Exception {
		if (SS.SORT_ALL_FOLDER.equals(filters)) {
			Indexclient.getinstance(namespace, index).filters(1).add(asc_seq).create(key, position%Integer.parseInt(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_CORE)
				.read("itemsonepage")), version);
		} else {
			String[] fs = CS.splitenc(filters);
			Indexclient ic = Indexclient.getinstance(namespace, index).filters(fs.length+1).add(asc_seq);
			for (String filter:fs) {
				ic.add(filter);
			}
			ic.create(key, position%Integer.parseInt(Configclient.getinstance(namespace, CS.REMOTE_CONFIG_CORE)
					.read("itemsonepage")), version);
		}
	}

}
