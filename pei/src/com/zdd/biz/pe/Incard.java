package com.zdd.biz.pe;

import java.util.ArrayList;
import java.util.List;

//cardvalue=displayvalue:1=3,2=4,3=5,4=6,5=7,6=8,7=9,8=10,9=j,10=q,11=k,12=A,13=2,14=b,15=B
public class Incard {
	
	public boolean valid(List<Integer> cardvals, List<Integer> playervals) {
		if (cardvals==null||playervals==null||cardvals.isEmpty()||playervals.isEmpty()) {
			return false;
		}
		if (cardvals.size()!=playervals.size()) {
			return false;
		}
		if (cardvals.size()==1&&(cardvals.get(0)==14||cardvals.get(0)==15)) {
			return false;
		}
		Integer distance = -1;
		for (int i=0;i<cardvals.size();i++) {
			if (cardvals.get(i)!=playervals.get(i)&&cardvals.get(i)!=14&&cardvals.get(i)!=15) {
				return false;
			}
			if (i<playervals.size()-1) {
				if (playervals.get(i)>playervals.get(i+1)) {
					return false;
				}
				if (distance == -1) {
					distance=playervals.get(i+1)-playervals.get(i);
				}
				if (distance!=0&&distance!=1) {
					return false;
				}
				if (distance!=playervals.get(i+1)-playervals.get(i)) {
					return false;
				}
			}
		}
		
		if (distance==1) {
			type = 1;
		}
		
		if (type==0&&cardvalues.size()>4) {
			return false;
		}
		
		if (type==1&&playervals.get(playervals.size()-1)==13) {
			return false;
		}
		
		cardvalues = cardvals;
		minplayervalue = playervals.get(0);
		
		if (cardvalues.size()==2&&minplayervalue==14) {
			twoboss = 1;
		}
		if (twoboss!=1&&type==1&&cardvalues.size()==2) {
			return false;
		}
		
		return true;
	}
	@SuppressWarnings("unused")
	private Incard() {}
	public Incard(String playerid, Integer playerindex) {
		this.playerid = playerid;
		this.playerindex = playerindex;
	}
	private String playerid="";
	public String playerid() {
		return playerid;
	}
	public Integer playerindex = -1;
	public Integer twoboss = 0;//0:not 2 bosses; 1:only 2 bosses;
	public String nick="";
	public Integer type=0;//0:same;1:consequent
	public List<Integer> cardvalues=new ArrayList<Integer>();
	public Integer minplayervalue=0;
}
