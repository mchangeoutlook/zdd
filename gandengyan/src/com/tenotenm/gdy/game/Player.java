package com.tenotenm.gdy.game;

import java.util.Vector;

import com.tenotenm.gdy.util.Common;
public class Player {
	private String name="无名氏";
	private String id = Common.generateid();
	private int seatno = -1;
	private long score = 0;
	private Vector<Integer> cards = new Vector<Integer>(Judge.maxnumofcards);
	public Player(int theseatno) throws Exception {
		if (theseatno<0||theseatno>=Judge.maxnumofseats) {
			throw new Exception("这个座位适合观战");
		}
		seatno = theseatno;
	}
	public int getseatno() {
		return seatno;
	}
	public String getname() {
		return name;
	}
	public void setname(String thename) {
		if (thename!=null&&!thename.trim().isEmpty()) {
			name = Common.saygood(thename.trim());
		}
	}
	public long getscore() {
		return score;
	}
	public void setscore(long thescore) throws Exception {
		if (thescore<score) {
			throw new Exception("得分只能水涨船高");
		}
		score = thescore;
	}
	public String getid() {
		return id;
	}
	
	public int playcard(int cardvalue) throws Exception {
		if (cards.indexOf(cardvalue)==-1) {
			throw new Exception("出老千");
		}
		return cards.remove(cards.indexOf(cardvalue));
	}
	
	public int addcard(int cardvalue) throws Exception {
		if (cardvalue<Judge.mincardvalue||cardvalue>Judge.maxcardvalue||cards.size()>Judge.maxnumofcards) {
			throw new Exception("这是千王之王的牌");
		}
		if (cards.isEmpty()) {
			cards.add(cardvalue);
			return 0;
		} else {
			if (cards.get(0)>=cardvalue) {
				cards.add(0, cardvalue);
				return 0;
			} else if (cards.get(cards.size()-1)<=cardvalue) {
				cards.add(cardvalue);
				return cards.size()-1;
			} else {
				int insert = 0;
				for (int i=cards.size()-1;i>=0;i--) {
					if (cards.get(i)>=cardvalue) {
						insert = i;
					} else {
						break;
					}
				}
				cards.add(insert, cardvalue);
				return insert;
			}
		}
	}
	public Vector<Integer> cardsinhand(){
		return cards;
	}
	public void roundover() {
		cards.clear();
	}
	
}
