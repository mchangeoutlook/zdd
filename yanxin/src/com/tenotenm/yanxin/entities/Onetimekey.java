package com.tenotenm.yanxin.entities;

import com.tenotenm.yanxin.util.Reuse;

public class Onetimekey extends Superentity{
	
	private String key=null;
	
	public String filter() {
		return Reuse.filter_paged;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
