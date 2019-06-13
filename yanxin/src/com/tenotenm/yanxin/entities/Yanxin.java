package com.tenotenm.yanxin.entities;

import java.util.Date;

import com.tenotenm.yanxin.util.Reuse;
import com.tenotenm.yanxin.util.Secret;

public class Yanxin extends Superentity{
	
	private String key=null;
	
	private String yxloginkey=null;
	
	private String uniquekeyprefix=null;
	
	private Date timecreate=null;
	private String emotion=null;
	private String weather=null;
	private String location=null;
	private String photo=null;
	private String photosmall=null;
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
		return new Object[] {weather, calcextravaluecapcity(weather, 100)};
	}
	protected String add4modify_weather() {
		if (weather==null) {
			return null;
		}
		return weather;
	}

	protected void read_emotion(String emotion) {
		this.emotion=emotion;
	}
	protected Object[] add4create_emotion() {
		if (emotion==null) {
			return null;
		}
		return new Object[] {emotion, calcextravaluecapcity(emotion, 100)};
	}
	protected String add4modify_emotion() {
		if (emotion==null) {
			return null;
		}
		return emotion;
	}

	protected void read_location(String location) {
		this.location=location;
	}
	protected Object[] add4create_location() {
		if (location==null) {
			return null;
		}
		return new Object[] {location, calcextravaluecapcity(location, 200)};
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

	protected void read_photosmall(String photosmall) {
		this.photosmall=photosmall;
	}
	protected Object[] add4create_photosmall() {
		if (photosmall==null) {
			return null;
		}
		return new Object[] {photosmall, calcextravaluecapcity(photosmall, 51)};
	}
	protected String add4modify_photosmall() {
		if (photosmall==null) {
			return null;
		}
		return photosmall;
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
		return Secret.dec(weather);
	}
	public void setWeather(String weather) throws Exception {
		this.weather = Secret.enc(weather);
		if (this.weather.length()>100) {
			throw new Exception(Reuse.msg_hint+"天气未保存，天气字数过多");
		}
	}
	public String getEmotion() {
		return Secret.dec(emotion);
	}
	public void setEmotion(String emotion) throws Exception {
		this.emotion = Secret.enc(emotion);
		if (this.emotion.length()>100) {
			throw new Exception(Reuse.msg_hint+"心情未保存，心情字数过多");
		}
	}
	public String getLocation() {
		return Secret.dec(location);
	}
	public void setLocation(String location) throws Exception {
		this.location = Secret.enc(location);
		if (this.location.length()>200) {
			throw new Exception(Reuse.msg_hint+"地点未保存，地点字数过多");
		}
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) throws Exception {
		if (photo.length()>51) {
			throw new Exception(Reuse.msg_hint+"大图未保存，图片路径过长");
		}
		this.photo = photo;
	}

	public String getPhotosmall() {
		return photosmall;
	}
	public void setPhotosmall(String photosmall) throws Exception {
		if (photosmall.length()>51) {
			throw new Exception(Reuse.msg_hint+"小图未保存，图片路径过长");
		}
		this.photosmall = photosmall;
	}

	public String getContent() {
		return Secret.dec(content);
	}
	public void setContent(String content) throws Exception {
		this.content = Secret.enc(content);
		if (this.content.length()>6000) {
			throw new Exception(Reuse.msg_hint+"日记未保存，日记字数过多");
		}
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
