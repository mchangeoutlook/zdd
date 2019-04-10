package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;

public class Yxyanxin extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;
	
	private Date timecreate=null;
	private String weather=null;
	private String location=null;
	private String photo=null;
	private String content=null;
	
	protected void read_yxloginkey(String yxloginkey) {
		this.yxloginkey=yxloginkey;
	}
	protected Object[] add4create_yxloginkey() {
		if (yxloginkey==null) {
			return null;
		}
		return new Object[] {yxloginkey, 0};
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
	
	protected void read_weather(String weather) {
		this.weather=weather;
	}
	protected Object[] add4create_weather() {
		if (weather==null) {
			return null;
		}
		return new Object[] {weather, 30};
	}
	protected String add4modify_weather() {
		if (weather==null) {
			return null;
		}
		return weather;
	}
	
	protected void read_location(String location) {
		this.location=location;
	}
	protected Object[] add4create_location() {
		if (location==null) {
			return null;
		}
		return new Object[] {location, 300};
	}
	protected String add4modify_location() {
		if (location==null) {
			return null;
		}
		return location;
	}
	
	protected void read_photo(String photo) {
		this.photo=photo;
	}
	protected Object[] add4create_photo() {
		if (photo==null) {
			return null;
		}
		return new Object[] {photo, 0};
	}
	protected String add4modify_photo() {
		if (photo==null) {
			return null;
		}
		return photo;
	}
	
	protected void read_content(String content) {
		this.content=content;
	}
	protected Object[] add4create_content() {
		if (content==null) {
			return null;
		}
		return new Object[] {content, 3000};
	}
	protected String add4modify_content() {
		if (content==null) {
			return null;
		}
		return content;
	}
	
	public String getYxloginkey() {
		return yxloginkey;
	}
	public void setYxloginkey(String yxloginkey) {
		this.yxloginkey = yxloginkey;
	}
	public Date getTimecreate() {
		return timecreate;
	}
	public void setTimecreate(Date timecreate) {
		this.timecreate = timecreate;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
