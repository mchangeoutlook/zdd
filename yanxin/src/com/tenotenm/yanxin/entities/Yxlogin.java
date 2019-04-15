package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Yxlogin extends Superentity{
	
	private String key=null;
	
	public String yxaccountkey=null;
	
	private String ip=null;
	
	private String ua=null;
	
	private Date timecreate=new Date();
	
	private Date timeupdate=timecreate;
	
	private Boolean islogout=false;
	private static final String LOGOUT = "o";
	private static final String LOGIN = "i";
	
	protected void read_yxaccountkey(String yxaccountkey) {
		this.yxaccountkey=yxaccountkey;
	}
	protected Object[] add4create_yxaccountkey() {
		if (yxaccountkey==null) {
			return null;
		}
		return new Object[] {yxaccountkey, 0};
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

	protected void read_ua(String ua) {
		this.ua=ua;
	}
	protected Object[] add4create_ua() {
		if (ua==null) {
			return null;
		}
		return new Object[] {ua, 0};
	}

	protected void read_timecreate(String timecreate) {
		this.timecreate=Reuse.yyyyMMddHHmmss(timecreate);
	}
	protected Object[] add4create_timecreate() {
		if (timecreate==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(timecreate), 0};
	}
	
	protected void read_timeupdate(String timeupdate) {
		this.timeupdate=Reuse.yyyyMMddHHmmss(timeupdate);
	}
	protected Object[] add4create_timeupdate() {
		if (timeupdate==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(timeupdate), 0};
	}
	protected String add4modify_timeupdate() {
		if (timeupdate==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(timeupdate);
	}
	
	protected void read_islogout(String islogout) {
		this.islogout=LOGOUT.equals(islogout);
	}
	protected Object[] add4create_islogout() {
		if (islogout==null) {
			return null;
		}
		return new Object[] {islogout?LOGOUT:LOGIN, 0};
	}
	protected String add4modify_islogout() {
		if (islogout==null) {
			return null;
		}
		return islogout?LOGOUT:LOGIN;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getYxaccountkey() {
		return yxaccountkey;
	}
	public void setYxaccountkey(String yxaccountkey) {
		this.yxaccountkey = yxaccountkey;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUa() {
		return ua;
	}
	public void setUa(String ua) {
		this.ua = ua;
	}
	public Date getTimecreate() {
		return timecreate;
	}
	public void setTimecreate(Date timecreate) {
		this.timecreate = timecreate;
	}
	public Date getTimeupdate() {
		return timeupdate;
	}
	public void setTimeupdate(Date timeupdate) {
		this.timeupdate = timeupdate;
	}
	public Boolean getIslogout() {
		return islogout;
	}
	public void setIslogout(Boolean islogout) {
		this.islogout = islogout;
	}
	
}
