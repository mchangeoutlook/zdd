package com.zdd.bdc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

public class Bizparams {
	private String logink = null;
	private String accountk = null;
	public Bizparams(String loginkey) throws Exception {
		if (loginkey==null) {
			throw new Exception("");
		}
		logink = loginkey;
	}
	private Map<String, String> textparams = new Hashtable<String, String>(20);
	private Map<String, InputStream> fileparams = new Hashtable<String, InputStream>(20);
	public String getloginkey() {
		return logink;
	}
	public String getaccountkey() {
		return accountk;
	}
	
	public String getext(String name) {
		return textparams.get(name);
	}
	public InputStream getfile(String name) {
		return fileparams.get(name);
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
	public Map<String, String> valid(Map<String, String> rules) {
		Map<String, String> returnvalue = new Hashtable<String, String>(rules.size());
		for (String key:rules.keySet()) {
			
		}
		return returnvalue;
	}
	public boolean auth(String resourceid, String action) {
		
		return true;
	}
}
