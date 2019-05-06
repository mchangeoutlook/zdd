package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Yxaccount extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;

	private String yxyanxinuniquekeyprefix=null;

	private String name=null;
	
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
		return new Object[] {yxloginkey, calcextravaluecapcity(yxloginkey, 40)};
	}
	protected String add4modify_yxloginkey() {
		if (yxloginkey==null) {
			return null;
		}
		return yxloginkey;
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
	
	protected void read_name(String name) {
		this.name=name;
	}
	protected Object[] add4create_name() {
		if (name==null) {
			return null;
		}
		return new Object[] {name, 0};
	}
	
	protected void read_pass(String pass) {
		this.pass=pass;
	}
	protected Object[] add4create_pass() {
		if (pass==null) {
			return null;
		}
		return new Object[] {pass, 0};
	}
	protected String add4modify_pass() {
		if (pass==null) {
			return null;
		}
		return pass;
	}
	
	protected void read_motto(String motto) {
		this.motto=motto;
	}
	protected Object[] add4create_motto() {
		if (motto==null) {
			return null;
		}
		return new Object[] {motto, 0};
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
		return new Object[] {ip, 50};
	}
	protected String add4modify_ip() {
		if (ip==null) {
			return null;
		}
		return ip;
	}
	
	protected void read_ua(String ua) {
		this.ua=ua;
	}
	protected Object[] add4create_ua() {
		if (ua==null) {
			return null;
		}
		return new Object[] {ua, 200};
	}
	protected String add4modify_ua() {
		if (ua==null) {
			return null;
		}
		return ua;
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
	protected String add4modify_timecreate() {
		if (timecreate==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(timecreate);
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
	
	protected void read_timeexpire(String timeexpire) {
		this.timeexpire=Reuse.yyyyMMddHHmmss(timeexpire);
	}
	protected Object[] add4create_timeexpire() {
		if (timeexpire==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(timeexpire), 0};
	}
	protected String add4modify_timeexpire() {
		if (timeexpire==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(timeexpire);
	}
	
	protected void read_timewrongpass(String timewrongpass) {
		this.timewrongpass=Reuse.yyyyMMddHHmmss(timewrongpass);
	}
	protected Object[] add4create_timewrongpass() {
		if (timewrongpass==null) {
			return null;
		}
		return new Object[] {Reuse.yyyyMMddHHmmss(timewrongpass), 0};
	}
	protected String add4modify_timewrongpass() {
		if (timewrongpass==null) {
			return null;
		}
		return Reuse.yyyyMMddHHmmss(timewrongpass);
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

	public String getName() {
		return name;
	}

	public String getUniquename() {
		return name.toLowerCase().trim();
	}

	public void setName(String name) throws Exception {
		name = name.trim();
		if (name.length()<4||name.length()>20) {
			throw new Exception("账号长度需在4到20之间");
		}
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) throws Exception {
		if (pass.length()<4||pass.length()>20) {
			throw new Exception("密码长度需在4到20之间");
		}
		boolean isallsamechar = true;
		for (int i=0;i<pass.length();i++) {
			if (i<pass.length()-1&&pass.charAt(i)!=pass.charAt(i+1)) {
				isallsamechar = false;
				break;
			}
		}
		if (isallsamechar) {
			throw new Exception("密码太简单了，请重新填写密码，密码也可以是中文");
		}
		this.pass = Reuse.sign(pass);
	}
	
	public boolean ispasssame(String pass) {
		if (pass!=null&&Reuse.sign(pass).equals(this.pass)) {
			return true;
		} else {
			return false;
		}
	}
	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) throws Exception {
		motto = motto.toLowerCase().trim();
		if (motto.length()<4||motto.length()>100) {
			throw new Exception("格言长度需在4到100之间");
		}
		this.motto = Reuse.sign(motto);
	}
	
	public boolean ismottosame(String motto) {
		if (motto!=null&&Reuse.sign(motto.toLowerCase().trim()).equals(this.motto)) {
			return true;
		} else {
			return false;
		}
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
		if (ua.length()>200) {
			this.ua = ua.substring(0,200);
		} else {
			this.ua = ua;
		}
	}

	public Date getTimecreate() {
		return timecreate;
	}

	public void setTimecreate(Date timecreate) {
		this.timecreate = timecreate;
		this.timeupdate = timecreate;
		this.timeexpire = new Date(timecreate.getTime() + Reuse.getdaysmillisconfig("freeuse.days"));
		this.timewrongpass = new Date(timecreate.getTime() - Reuse.getsecondsmillisconfig("wrongpass.wait.seconds"));
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
