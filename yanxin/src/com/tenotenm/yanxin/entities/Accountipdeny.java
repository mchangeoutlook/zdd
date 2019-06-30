package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Accountipdeny extends Superentity{
	
	private String key=null;
	
	private Date wrongpasstime=null;
	private String wrongpassip=null;
	private long wrongpasstimes=0l;
	private Long wrongpasstimes4increment=0l;
	
	public String filter() {
		return Reuse.filter_paged;
	}
	
	protected void read_wrongpassip(String wrongpassip) {
		this.wrongpassip=wrongpassip;
	}
	protected Object[] add4create_wrongpassip() {
		if (wrongpassip==null) {
			return null;
		}
		return new Object[] {wrongpassip, 50};
	}
	protected String add4modify_wrongpassip() {
		if (wrongpassip==null) {
			return null;
		}
		return wrongpassip;
	}

	protected void read_wrongpasstimes(String wrongpasstimes) {
		this.wrongpasstimes=Long.parseLong(wrongpasstimes);
	}
	protected Long add4increment_wrongpasstimes() {
		if (wrongpasstimes4increment==null) {
			return null;
		}
		return wrongpasstimes4increment;
	}

	protected void read_wrongpasstime(String wrongpasstime) {
		this.wrongpasstime=Reuse.yyyyMMddHHmmss(wrongpasstime);
	}
	protected Object[] add4create_wrongpasstime() {
		if (wrongpasstime==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(wrongpasstime), 0};
	}
	protected String add4modify_wrongpasstime() {
		if (wrongpasstime==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(wrongpasstime);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getWrongpassip() {
		return wrongpassip;
	}

	public void setWrongpassip(String wrongpassip) {
		this.wrongpassip = wrongpassip;
	}

	public Date getWrongpasstime() {
		return wrongpasstime;
	}

	public void setWrongpasstime(Date wrongpasstime) {
		this.wrongpasstime = wrongpasstime;
	}

	public Long getWrongpasstimes() {
		return wrongpasstimes;
	}

	public void setWrongpasstimes(Long wrongpasstimes) {
		this.wrongpasstimes = wrongpasstimes;
	}
	
	public Long getWrongpasstimes4increment() {
		return wrongpasstimes4increment;
	}
	public void setWrongpasstimes4increment(Long wrongpasstimes4increment) {
		this.wrongpasstimes4increment = wrongpasstimes4increment;
	}
}
