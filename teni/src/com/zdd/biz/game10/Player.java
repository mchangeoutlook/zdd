package com.zdd.biz.game10;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.zdd.util.Saysth;

public class Player{
	private String id=UUID.randomUUID().toString().replaceAll("-", "");
	public Integer index = 0;
	public String nick=String.valueOf(System.currentTimeMillis()).substring(10)+"nick";
	public Long wins = 0l;
	public List<Integer> mycards = new ArrayList<Integer>(100);
	@SuppressWarnings("unused")
	private Player() {}
	public Player(String nick) {
		if (nick!=null&&!nick.trim().isEmpty()) {
			this.nick = Saysth.sayhtml(nick, 10);
		}
	}
	public String id() {
		return id;
	}
	public void in(Integer card) {
		if (mycards.isEmpty()||mycards.get(mycards.size()-1)<=card) {
			mycards.add(card);
		} else {
			for (int i=0;i<mycards.size();i++) {
				if (mycards.get(i)>=card) {
					mycards.add(i,card);
					break;
				}
			}
		}
	}
	public void out(Integer card) {
		mycards.remove(mycards.indexOf(card));
	}
	
}
