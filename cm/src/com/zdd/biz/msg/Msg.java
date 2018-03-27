package com.zdd.biz.msg;

import com.zdd.util.Saysth;

public class Msg {
	public String sendernick= "";
	public String msg = "";
	public Long sendtime = System.currentTimeMillis();
	public Msg(String msg) {
		this.msg = Saysth.sayhtml(msg,20);
	}
	@SuppressWarnings("unused")
	private Msg() {}
}
