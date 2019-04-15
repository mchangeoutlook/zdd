package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Ipdeny extends Superentity{
	
	private String key=null;
	
	private String ip=null;
	
	private Date timedeny=new Date();
	
	protected void read_ip(String ip) {
		this.ip=ip;
	}
	protected Object[] add4create_ip() {
		if (ip==null) {
			return null;
		}
		return new Object[] {ip, 0};
	}


	protected void read_timedeny(String timedeny) {
		this.timedeny=Reuse.yyyyMMddHHmmss(timedeny);
	}
	protected Object[] add4create_timedeny() {
		if (timedeny==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(timedeny), 0};
	}
	
	protected String add4modify_timedeny() {
		if (timedeny==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(timedeny);
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

	public Date getTimedeny() {
		return timedeny;
	}

	public void setTimedeny(Date timedeny) {
		this.timedeny = timedeny;
	}
	
}
