package com.zdd.biz.pe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.zdd.util.Saysth;

public class Player{
	private String id=UUID.randomUUID().toString().replaceAll("-", "");
	public Integer noplay = 2;
	public Integer autoplay = 0;
	public Integer prevaction = 0;//0:pass
	public Integer index = 0;
	public Integer addedcardindex  =-1;
	public String nick=String.valueOf(System.currentTimeMillis()).substring(10)+"nick";
	public Long losts = 0l;
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
		addedcardindex = mycards.indexOf(card);
	}
	public void out(Incard incard) {
		prevaction = 1;
		addedcardindex = -1;
		for (Integer card:incard.cardvalues) {
			mycards.remove(mycards.indexOf(card));
		}
	}
	
}
