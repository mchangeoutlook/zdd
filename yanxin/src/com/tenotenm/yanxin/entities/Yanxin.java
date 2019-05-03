package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;
import com.tenotenm.yanxin.util.Secret;

public class Yanxin extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;
	
	private String uniquekeyprefix=null;
	
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

	protected void read_uniquekeyprefix(String uniquekeyprefix) {
		this.uniquekeyprefix=uniquekeyprefix;
	}
	protected Object[] add4create_uniquekeyprefix() {
		if (uniquekeyprefix==null) {
			return null;
		}
		return new Object[] {uniquekeyprefix, 0};
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
	
	protected void read_weather(String weather) {
		this.weather=weather;
	}
	protected Object[] add4create_weather() {
		if (weather==null) {
			return null;
		}
		return new Object[] {weather, calcextravaluecapcity(weather, 30)};
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
		return new Object[] {location, calcextravaluecapcity(location, 300)};
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
		return new Object[] {photo, calcextravaluecapcity(photo, 51)};
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
		return new Object[] {content, calcextravaluecapcity(content, 6000)};
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
	public String getUniquekeyprefix() {
		return uniquekeyprefix;
	}
	public void setUniquekeyprefix(String uniquekeyprefix) {
		this.uniquekeyprefix = uniquekeyprefix;
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
	public void setWeather(String weather) throws Exception {
		if (weather.length()>10) {
			throw new Exception("天气限10字");
		}
		this.weather = weather;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) throws Exception {
		if (location.length()>20) {
			throw new Exception("地点限20字");
		}
		this.location = location;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) throws Exception {
		if (photo.length()>51) {
			throw new Exception("图片路径限51字");
		}
		this.photo = photo;
	}
	public String getContent() {
		return Secret.dec(content);
	}
	public void setContent(String content) throws Exception {
		if (content.length()>1000) {
			throw new Exception("日记限1000字");
		}
		this.content = Secret.enc(content);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
