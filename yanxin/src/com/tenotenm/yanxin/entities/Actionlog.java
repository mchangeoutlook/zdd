package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Actionlog extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;
	
	private Date timecreate = new Date();
	
	private String uniqueaccountnamefrom=null;
	
	private String uniqueaccountnameto=null;
	
	private String action=null;
	
	private String oldvalue=null;
	
	private String newvalue=null;
	

	public String filter() {
		return Reuse.filter_paged;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public Date getTimecreate() {
		return timecreate;
	}

	public void setTimecreate(Date timecreate) {
		this.timecreate = timecreate;
	}
	
	protected void read_yxloginkey(String yxloginkey) {
		this.yxloginkey=yxloginkey;
	}
	protected Object[] add4create_yxloginkey() {
		if (yxloginkey==null) {
			return null;
		}
		return new Object[] {yxloginkey, 0};
	}
	public String getYxloginkey() {
		return yxloginkey;
	}

	public void setYxloginkey(String yxloginkey) {
		this.yxloginkey = yxloginkey;
	}

	protected void read_uniqueaccountnamefrom(String uniqueaccountnamefrom) {
		this.uniqueaccountnamefrom=uniqueaccountnamefrom;
	}
	protected Object[] add4create_uniqueaccountnamefrom() {
		if (uniqueaccountnamefrom==null) {
			return null;
		}
		return new Object[] {uniqueaccountnamefrom, 0};
	}

	public String getUniqueaccountnamefrom() {
		return uniqueaccountnamefrom;
	}

	public void setUniqueaccountnamefrom(String uniqueaccountnamefrom) {
		this.uniqueaccountnamefrom = uniqueaccountnamefrom;
	}
	protected void read_uniqueaccountnameto(String uniqueaccountnameto) {
		this.uniqueaccountnameto=uniqueaccountnameto;
	}
	protected Object[] add4create_uniqueaccountnameto() {
		if (uniqueaccountnameto==null) {
			return null;
		}
		return new Object[] {uniqueaccountnameto, 0};
	}

	public String getUniqueaccountnameto() {
		return uniqueaccountnameto;
	}

	public void setUniqueaccountnameto(String uniqueaccountnameto) {
		this.uniqueaccountnameto = uniqueaccountnameto;
	}
	

	protected void read_action(String action) {
		this.action=action;
	}
	protected Object[] add4create_action() {
		if (action==null) {
			return null;
		}
		return new Object[] {action, 0};
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	protected void read_oldvalue(String oldvalue) {
		this.oldvalue=oldvalue;
	}
	protected Object[] add4create_oldvalue() {
		if (oldvalue==null) {
			return null;
		}
		return new Object[] {oldvalue, 0};
	}

	public String getOldvalue() {
		return oldvalue;
	}

	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
	}
	protected void read_newvalue(String newvalue) {
		this.newvalue=newvalue;
	}
	protected Object[] add4create_newvalue() {
		if (newvalue==null) {
			return null;
		}
		return new Object[] {newvalue, 0};
	}

	public String getNewvalue() {
		return newvalue;
	}

	public void setNewvalue(String newvalue) {
		this.newvalue = newvalue;
	}
}
