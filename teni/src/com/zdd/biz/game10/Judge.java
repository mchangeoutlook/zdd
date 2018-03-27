package com.zdd.biz.game10;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.zdd.biz.game10.Longpollnotify;
import com.zdd.biz.game10.Player;

public class Judge{
	public String id=UUID.randomUUID().toString().replaceAll("-", "");
	private Long refreshtime = System.currentTimeMillis();
	public List<Incard> incards = new ArrayList<Incard>(100);
	private List<Player> players = new ArrayList<Player>(20);
	private Map<String, Player> playeridplayer = new HashMap<String, Player>();
	public Integer started = 0;
	private Integer restplayers = 0;
	public Integer round = 0;
	public Integer nextplayerindex = 0;
	public Integer nextaction = 0;//0:dispatch;1:incard;2:judge;
	public Integer settingadvance = 0;//0:+=10 1:+-*/=10
	
	public boolean expire() {
		return System.currentTimeMillis()-refreshtime>10*60*1000;
	}
	public Long refreshtime() {
		return refreshtime;
	}
	public Player getplayer(String playerid) {
		return playeridplayer.get(playerid);
	}
	public List<Player> players(){
		return players;
	}
	
	public synchronized String joinordispatchcard(String playerid, String nick) throws Exception {
		refreshtime = System.currentTimeMillis();
		boolean tojoin = playerid==null&&nick!=null;
		boolean todispatchcard = nick==null&&playerid!=null;
		if (!tojoin&&!todispatchcard||tojoin&&todispatchcard) {
			throw new Exception("wrongparams");
		}
		if (tojoin&&round !=0||todispatchcard&&started==1) {
			throw new Exception("started");
		}
		if (todispatchcard) {
			if (players.size()<2){
				throw new Exception("notenoughplayers");
			} 
			if (getplayer(playerid).index!=nextplayerindex||nextaction!=0){
				throw new Exception("notyourturn");
			}
			
			int numofcardsets = 0;
			int numofplayers = players.size();
			if (numofplayers>=4&&numofplayers<=6) {
				numofcardsets=1;
			}
			if (numofplayers>=7&&numofplayers<=11) {
				numofcardsets=2;
			}
			if (numofplayers>=12&&numofplayers<=18) {
				numofcardsets=3;
			}
			if (numofplayers>=19) {
				numofcardsets=4;
			}

			int cardsets = 1;
			if (numofcardsets!=0) {
				cardsets = numofcardsets;
			}
			List<Integer> allcards = new ArrayList<Integer>(cardsets*54);
			for (int i=0;i< cardsets*13;i++) {
				if (i%13==0) {
					allcards.add(14);
					allcards.add(15);
				}
				allcards.add(i%13+1);
				allcards.add(i%13+1);
				allcards.add(i%13+1);
				allcards.add(i%13+1);
			}
			int i = 0;
			for (Player p: players) {
				if (p.index==nextplayerindex) {
					break;
				}
				i++;
			}
			while (!allcards.isEmpty()) {
				if (i==players.size()) {
					i=0;
				}
				players.get(i).in(allcards.remove(new Random().nextInt(allcards.size())));
				i++;
				if (numofcardsets==0&&allcards.size()==27) {
					break;
				}
			}
			
			restplayers = players.size();
			started = 1;
			nextaction=1;
			round++;
			Longpollnotify.donotify(this, Longpollnotify.ACTION_DISPATCH, null);
		}
		if (tojoin) {
			if (players.size()>108){
				throw new Exception("toomanyplayers");
			} 
			if (nick!=null&&!nick.trim().isEmpty()) {
				for (Player p: players) {
					if (p.nick.equals(nick)) {
						throw new Exception("duplicatenick");
					}
				}
			}
			Player newplayer = new Player(nick);
			players.add(newplayer);
			newplayer.index=players.indexOf(newplayer);
			playeridplayer.put(newplayer.id(), newplayer);
			Longpollnotify.donotify(this, Longpollnotify.ACTION_JOIN, null);
			return newplayer.id();
		}
		return "";
	}

	public synchronized void in(String playerid, Integer card) throws Exception {
		refreshtime = System.currentTimeMillis();
		if (started==0) {
			throw new Exception("notstarted");
		}
		if (getplayer(playerid).index!=nextplayerindex||nextaction!=1){
			throw new Exception("notyourturn");
		}
		
		Player p = getplayer(playerid);
		p.out(card);
		Incard incard = new Incard();
		incard.nick=p.nick;
		incard.value=card;
		incards.add(incard);
		incard.index = incards.indexOf(incard);
		int winstartindex=-1;
		if (card >= 10) {
			if (card == 10||card==14||card==15) {
				winstartindex = 0;
			} else {
				for (int i=0;i<incards.size()-1;i++) {
					if (incards.get(i).value==card) {
						winstartindex = i;
						break;
					}
				}
			}
		} else {
			for (int i=0;i<incards.size()-1;i++) {
				if (incards.get(i).value<10&&prejudge(i)) {
					winstartindex = -2;
					break;
				}
			}
		}
		if (p.mycards.isEmpty()) {
			restplayers--;
		}
		next(playerid, winstartindex);
	}
	
	private boolean prejudge(Integer fromcardindex) {
		boolean returnvalue = false;
		if (incards.get(fromcardindex).value+incards.get(incards.size()-1).value==10) {
			returnvalue = true;
		} else if (incards.get(fromcardindex).value+incards.get(incards.size()-1).value<10){
			List<Integer> temp = new ArrayList<Integer>();
			for (int i = fromcardindex+1; i< incards.size()-1;i++) {
				if (incards.get(fromcardindex).value+incards.get(incards.size()-1).value+incards.get(i).value==10) {
					returnvalue = true;
				} else if (incards.get(fromcardindex).value+incards.get(incards.size()-1).value+incards.get(i).value<10) {
					int tsize = temp.size();
					for (int j = 0; j < tsize; j++) {
						if (temp.get(j)+incards.get(i).value==10) {
							returnvalue = true;
						} else if (temp.get(j)+incards.get(i).value<10){
							temp.add(temp.get(j)+incards.get(i).value);
						}
					}
					temp.add(incards.get(fromcardindex).value+incards.get(incards.size()-1).value+incards.get(i).value);
				}
			}
		}
		return returnvalue;
	}

	public synchronized void judge(String playerid, Integer cardindex) throws Exception {
		refreshtime = System.currentTimeMillis();
		if (started==0) {
			throw new Exception("notstarted");
		}
		if (getplayer(playerid).index!=nextplayerindex||nextaction!=2){
			throw new Exception("notyourturn");
		}
		
		int winstartindex = -3;
		if (cardindex>=0&&prejudge(cardindex)) {
			winstartindex = cardindex;
		} else if (cardindex<0){
			winstartindex = -1;
		}
		next(playerid, winstartindex);
	}
	
	//winstartindex>=0:win; winstartindex==-1:pass; winstartindex==-2:need judge; winstartindex==-3:wrong judge
	private void next(String playerid, int winstartindex) throws Exception {
		refreshtime = System.currentTimeMillis();
		
		Player p = getplayer(playerid);
		if (winstartindex >=0) {
			int win = incards.size()-winstartindex;
			p.wins += win;
			for (int i = 0; i < win;i++) {
				incards.remove(incards.size()-1);
			}
			if (win>2) {
				p.wins += win/2;
			}
		}
		if (winstartindex == -3) {
			p.wins--;
		}
		
		nextaction=2;
		if (winstartindex!=-2) {
			if (restplayers==0) {
				Long maxwin = 0l;
				for (int i = 0; i< players.size();i++) {
					if (players.get(i).wins>=maxwin) {
						maxwin = players.get(i).wins;
						nextplayerindex=i;
						nextaction=0;
					}
				}
				incards.clear();
				started = 0;
			} else {
				nextplayerindex++;
				if (nextplayerindex==players.size()) {
					nextplayerindex = 0;
				}
				nextaction=1;
			}
		}
		Longpollnotify.donotify(this, Longpollnotify.ACTION_INCARD, null);
	}
	
	public static void main(String[] s) throws Exception {
		 ScriptEngineManager factory = new ScriptEngineManager();  
		// create a JavaScript engine  
		ScriptEngine engine = factory.getEngineByName("JavaScript");  
		// evaluate JavaScript code from String  
		Object a= engine.eval("(1+2)*(3-1)*4/2");
		System.out.println(a.toString());
		System.out.println(",8,,9,,10,,13,,".matches(".*(,9,).*(,11,).*"));
		
	}
	
}
