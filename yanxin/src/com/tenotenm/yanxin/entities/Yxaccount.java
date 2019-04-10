package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Yxaccount extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;

	private String yxyanxinuniquekeyprefix=null;

	private String uniquename=null;
	
	private String pass=null;
	
	private String motto=null;
	
	private String ip=null;
	
	private String ua=null;
	
	private Date timecreate=new Date();
	
	private Date timeupdate=timecreate;
	
	private Date timewrongpass=null;
	
	private Date timeexpire=null;
	
	private Long daystogive=0l;

	private Long daystogive4increment=0l;


	protected void read_yxloginkey(String yxloginkey) {
		this.yxloginkey=yxloginkey;
	}
	protected Object[] add4create_yxloginkey() {
		if (yxloginkey==null) {
			return null;
		}
		return new Object[] {yxloginkey, 40};
	}
	
	protected void read_yxyanxinuniquekeyprefix(String yxyanxinuniquekeyprefix) {
		this.yxyanxinuniquekeyprefix=yxyanxinuniquekeyprefix;
	}
	protected Object[] add4create_yxyanxinuniquekeyprefix() {
		if (yxyanxinuniquekeyprefix==null) {
			return null;
		}
		return new Object[] {yxyanxinuniquekeyprefix, 0};
	}
	protected String add4modify_yxyanxinuniquekeyprefix() {
		if (yxyanxinuniquekeyprefix==null) {
			return null;
		}
		return yxyanxinuniquekeyprefix;
	}
	
	protected void read_uniquename(String uniquename) {
		this.uniquename=uniquename;
	}
	protected Object[] add4create_uniquename() {
		if (uniquename==null) {
			return null;
		}
		return new Object[] {uniquename, 0};
	}
	
	protected void read_pass(String pass) {
		this.pass=pass;
	}
	protected Object[] add4create_pass() {
		if (pass==null) {
			return null;
		}
		return new Object[] {Reuse.sign(pass), 60};
	}
	protected String add4modify_pass() {
		if (pass==null) {
			return null;
		}
		return Reuse.sign(pass);
	}
	
	protected void read_motto(String motto) {
		this.motto=motto;
	}
	protected Object[] add4create_motto() {
		if (motto==null) {
			return null;
		}
		return new Object[] {motto, 300};
	}
	protected String add4modify_motto() {
		if (motto==null) {
			return null;
		}
		return motto;
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
		this.timecreate=Reuse.yyyymmddhhmmss(timecreate);
	}
	protected Object[] add4create_timecreate() {
		if (timecreate==null) {
			return null;
		}
		return new Object[] {Reuse.yyyymmddhhmmss(timecreate), 0};
	}
	
	protected void read_timeupdate(String timeupdate) {
		this.timeupdate=Reuse.yyyymmddhhmmss(timeupdate);
	}
	protected Object[] add4create_timeupdate() {
		if (timeupdate==null) {
			return null;
		}
		return new Object[] {Reuse.yyyymmddhhmmss(timeupdate), 0};
	}
	protected String add4modify_timeupdate() {
		if (timeupdate==null) {
			return null;
		}
		return Reuse.yyyymmddhhmmss(timeupdate);
	}
	
	protected void read_timeexpire(String timeexpire) {
		this.timeexpire=Reuse.yyyymmddhhmmss(timeexpire);
	}
	protected Object[] add4create_timeexpire() {
		if (timeexpire==null) {
			return null;
		}
		return new Object[] {Reuse.yyyymmddhhmmss(timeexpire), 0};
	}
	protected String add4modify_timeexpire() {
		if (timeexpire==null) {
			return null;
		}
		return Reuse.yyyymmddhhmmss(timeexpire);
	}
	
	protected void read_timewrongpass(String timewrongpass) {
		this.timewrongpass=Reuse.yyyymmddhhmmss(timewrongpass);
	}
	protected Object[] add4create_timewrongpass() {
		if (timewrongpass==null) {
			return null;
		}
		return new Object[] {Reuse.yyyymmddhhmmss(timewrongpass), 0};
	}
	protected String add4modify_timewrongpass() {
		if (timewrongpass==null) {
			return null;
		}
		return Reuse.yyyymmddhhmmss(timewrongpass);
	}

	protected void read_daystogive(String daystogive) {
		this.daystogive=Long.parseLong(daystogive);
	}
	protected Long add4increment_daystogive() {
		if (daystogive4increment==null) {
			return null;
		}
		return daystogive4increment;
	}

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getYxloginkey() {
		return yxloginkey;
	}

	public void setYxloginkey(String yxloginkey) {
		this.yxloginkey = yxloginkey;
	}

	public String getUniquename() {
		return uniquename;
	}

	public void setUniquename(String uniquename) {
		this.uniquename = uniquename;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
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

	public Date getTimewrongpass() {
		return timewrongpass;
	}

	public void setTimewrongpass(Date timewrongpass) {
		this.timewrongpass = timewrongpass;
	}

	public Date getTimeexpire() {
		return timeexpire;
	}

	public void setTimeexpire(Date timeexpire) {
		this.timeexpire = timeexpire;
	}

	public Long getDaystogive() {
		return daystogive;
	}

	public void setDaystogive(Long daystogive) {
		this.daystogive = daystogive;
	}
	
	public Long getDaystogive4increment() {
		return daystogive4increment;
	}
	public void setDaystogive4increment(Long daystogive4increment) {
		this.daystogive4increment = daystogive4increment;
	}
	public String getYxyanxinuniquekeyprefix() {
		return yxyanxinuniquekeyprefix;
	}
	public void setYxyanxinuniquekeyprefix(String yxyanxinuniquekeyprefix) {
		this.yxyanxinuniquekeyprefix = yxyanxinuniquekeyprefix;
	}
	
	
	
}
