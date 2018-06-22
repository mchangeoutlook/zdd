package com.zdd.bdc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class Bizparams {
	private String logink = null;
	private String accountk = null;
	private String ipaddr = null;
	private String ua = null;
	public Bizparams(String loginkey, String accountkey, String ip, String useragent){
		logink = loginkey;
		accountk = accountkey;
		ipaddr = ip;
		ua = useragent;
	}
	private Map<String, String> textparams = new Hashtable<String, String>(20);
	private Map<String, InputStream> fileparams = new Hashtable<String, InputStream>(20);
	public String getloginkey() {
		return logink;
	}
	public String getaccountkey() {
		return accountk;
	}
	public String getip() {
		return ipaddr;
	}
	public String getuseragent() {
		return ua;
	}
	
	public String getext(String name) {
		return textparams.get(name);
	}
	public InputStream getfile(String name) {
		return fileparams.get(name);
	}
	public Set<String> getfilenames() {
		return fileparams.keySet();
	}
	public void add(String name, String text) {
		textparams.put(name, text);
	}
	public void add(String name, InputStream file) {
		fileparams.put(name, file);
	}
	public void clear() {
		textparams.clear();
		for (InputStream is: fileparams.values()) {
			try {
				is.close();
			} catch (IOException e) {
				//do nothing
			}
		}
		fileparams.clear();
	}
}
