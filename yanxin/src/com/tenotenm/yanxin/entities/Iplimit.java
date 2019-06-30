package com.tenotenm.yanxin.entities;

import com.tenotenm.yanxin.util.Reuse;

public class Iplimit extends Superentity{
	
	private String key=null;
	
	private String ip=null;
	
	private Long newaccounts=null;
	
	private Long newaccounts4increment=null;
	

	public String filter() {
		return Reuse.filter_paged;
	}
	
	protected void read_ip(String ip) {
		this.ip=ip;
	}
	protected Object[] add4create_ip() {
		if (ip==null) {
			return null;
		}
		return new Object[] {ip, 0};
	}

	protected void readtoday_newaccounts(String newaccounts) {
		this.newaccounts=Long.parseLong(newaccounts);
	}

	protected Long add4incrementtoday_newaccounts() {
		if (newaccounts4increment==null) {
			return null;
		}
		return newaccounts4increment;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getNewaccounts() {
		return newaccounts;
	}
	public void setNewaccounts(Long newaccounts) {
		this.newaccounts = newaccounts;
	}
	
	public Long getNewaccounts4increment() {
		return newaccounts4increment;
	}
	public void setNewaccounts4increment(Long newaccounts4increment) {
		this.newaccounts4increment = newaccounts4increment;
	}
	
	
}
