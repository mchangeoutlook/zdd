package com.zdd.biz.pe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Judge{
	public String id=UUID.randomUUID().toString().replaceAll("-", "");
	private Long refreshtime = System.currentTimeMillis();
	public List<Incard> incards = new ArrayList<Incard>(100);
	private List<Player> players = new ArrayList<Player>(20);
	private Map<String, Player> playeridplayer = new HashMap<String, Player>();
	public Integer started = 0;
	public Integer round = 0;
	public Integer nextplayerindex = 0;
	public Integer restcards = 54;
	public Integer autoin = 40;
	public Integer times = 1;
	private List<Integer> allcards = new ArrayList<Integer>();
	private List<Integer> morecards = new ArrayList<Integer>();
	
	public Integer settingallget=0;//0:player gets one, 1:all get one.
	
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
	
	public synchronized void auto() throws Exception {
		if (System.currentTimeMillis()-refreshtime<40000){
			autoin = (int) ((System.currentTimeMillis()-refreshtime)/10000+1)*10;
		} else {
			players.get(nextplayerindex).autoplay=1;
			if (started==0) {
				joinordispatchcard(players.get(nextplayerindex).id(),null);
			} else if (started==1) {
				Incard incard = null;
				if (incards.isEmpty()||incards.get(incards.size()-1).playerindex==nextplayerindex) {
					incard = new Incard(players.get(nextplayerindex).id(),nextplayerindex);
					Integer nobosses = 0;
					for (Integer i:players.get(nextplayerindex).mycards) {
						if (i!=14&&i!=15) {
							nobosses++;
						}
					}
					List<Integer> cardvals = new ArrayList<Integer>();
					List<Integer> playervals = new ArrayList<Integer>();
					
					if (nobosses>0) {
						cardvals.add(players.get(nextplayerindex).mycards.get(new Random().nextInt(nobosses)));
						playervals.add(cardvals.get(0));
					} else if (players.get(nextplayerindex).mycards.size()==2) {
						cardvals.addAll(players.get(nextplayerindex).mycards);
						playervals.addAll(players.get(nextplayerindex).mycards);
					}
					if (incard.valid(cardvals, playervals)) {
						incard.nick=players.get(nextplayerindex).nick;
					} else {
						incard = null;
					}
				}
				in(players.get(nextplayerindex).id(),incard);
			}
		}
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
			if (getplayer(playerid).index!=nextplayerindex){
				throw new Exception("notyourturn");
			}
			
			allcards.clear();
			incards.clear();

			for (int i=0;i< 13;i++) {
				if (i%13==0) {
					allcards.add(14);
					allcards.add(15);
				}
				allcards.add(i%13+1);
				allcards.add(i%13+1);
				allcards.add(i%13+1);
				allcards.add(i%13+1);
			}
			
			for (Player p: players) {
				p.mycards.clear();
				p.noplay=2;
				int cards = 5;
				if (p.index==nextplayerindex) {
					cards = 6;
				}
				for (int i=0;i<cards;i++) {
					p.in(allcards.remove(new Random().nextInt(allcards.size())));
				}
				p.addedcardindex=-1;	
			}
			
			int index = nextplayerindex;
			int max = players.size();
			do {
				Player p = players.get(index);
				if (!p.mycards.contains(13)) {
					int bosses = 0;
					if (p.mycards.contains(14)) {
						bosses++;
					}
					if (p.mycards.contains(15)) {
						bosses++;
					}
					int distance = -1;
					int size = p.mycards.size()-bosses;
					for (int i=0;i<size;i++) {
						if (i<size-1) {
							distance=p.mycards.get(i+1)-p.mycards.get(i);
							if (distance==0) {
								break;
							}
							if (distance==1) {
								continue;
							}
							if (distance-1<=bosses) {
								bosses=bosses-distance+1;
								distance=1;
							} else {
								break;
							}
						}
					}
					if (distance==1) {
						nextplayerindex = p.index;
						break;
					}
				}
				index++;
				if (index==players.size()) {
					index=0;
					max = nextplayerindex;
				}
			} while(index<max);
			
			started = 1;
			round++;
			autoin = 40;
			restcards = allcards.size();
			Longpollnotify.donotify(this, Longpollnotify.ACTION_DISPATCH, null);
		} else if (tojoin) {
			if (players.size()>8){
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
	
	private void autopass() {
		nextplayerindex++;
		if (nextplayerindex==players.size()) {
			nextplayerindex = 0;
		}
		if (incards.isEmpty()||incards.get(incards.size()-1).playerid().equals(players.get(nextplayerindex).id())) {
			return;
		}
		if (hastopass(players.get(nextplayerindex).mycards)) {
			autopass();
		}
	}
	
	private boolean hastopass(List<Integer> cards) {
		if (incards.get(incards.size()-1).twoboss==1) {
			return true;
		}
		if (hasconsequence(cards,14,2)) {
			return false;
		}
		if (incards.get(incards.size()-1).cardvalues.size()==1) {
			if (has3same(cards,0)||has4same(cards,0)) {
				return false;
			}
			if (incards.get(incards.size()-1).minplayervalue!=13&&
					(cards.contains(incards.get(incards.size()-1).minplayervalue+1)||cards.contains(13))) {
				return false;
			}
		} else if (incards.get(incards.size()-1).cardvalues.size()==2) {
			if (has3same(cards,0)||has4same(cards,0)) {
				return false;
			}
			if (incards.get(incards.size()-1).minplayervalue!=13&&
					(has2same(cards,13)||has2same(cards, incards.get(incards.size()-1).minplayervalue+1))) {
				return false;
			}
			
		} else if (incards.get(incards.size()-1).cardvalues.size()>2) {
			if (incards.get(incards.size()-1).type==1) {
				if (has3same(cards,0)||has4same(cards,0)) {
					return false;
				}
				if (hasconsequence(cards, incards.get(incards.size()-1).minplayervalue+1,incards.get(incards.size()-1).cardvalues.size())) {
					return false;
				}
			} else {
				if (incards.get(incards.size()-1).cardvalues.size()==3) {
					if (has3same(cards,incards.get(incards.size()-1).minplayervalue)||has4same(cards,0)) {
						return false;
					}
				}
				if (incards.get(incards.size()-1).cardvalues.size()==4) {
					if (has4same(cards,incards.get(incards.size()-1).minplayervalue)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean has2same(List<Integer> cards, Integer equalsvalue){
		if (cards.size()<2) {
			return false;
		}
		List<Integer> bosses = new ArrayList<Integer>();
		List<Integer> nobosses = new ArrayList<Integer>();
		for (Integer i:cards) {
			if (i==14||i==15) {
				bosses.add(i);
			} else {
				nobosses.add(i);
			}
		}
		
		if (bosses.size()>0&&nobosses.size()>0) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)==equalsvalue) {
					return true;
				}
			}
		} else if (bosses.size()==0) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)==equalsvalue&&i+1<nobosses.size()&&nobosses.get(i)==nobosses.get(i+1)) {
					return true;
				}
			}
		} 
		
		return false;
	}

	private boolean has3same(List<Integer> cards, Integer greaterthanvalue){
		if (cards.size()<3) {
			return false;
		}
		List<Integer> bosses = new ArrayList<Integer>();
		List<Integer> nobosses = new ArrayList<Integer>();
		for (Integer i:cards) {
			if (i==14||i==15) {
				bosses.add(i);
			} else {
				nobosses.add(i);
			}
		}
		if (bosses.size()==2) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)>greaterthanvalue) {
					return true;
				}
			}
		} else if (bosses.size()==1) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)>greaterthanvalue&&i+1<nobosses.size()&&nobosses.get(i)==nobosses.get(i+1)) {
					return true;
				}
			}
		} else if (bosses.size()==0) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)>greaterthanvalue&&i+2<nobosses.size()&&nobosses.get(i)==nobosses.get(i+1)&&nobosses.get(i+1)==nobosses.get(i+2)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean has4same(List<Integer> cards, Integer greaterthanvalue){
		if (cards.size()<4) {
			return false;
		}
		List<Integer> bosses = new ArrayList<Integer>();
		List<Integer> nobosses = new ArrayList<Integer>();
		for (Integer i:cards) {
			if (i==14||i==15) {
				bosses.add(i);
			} else {
				nobosses.add(i);
			}
		}
		
		if (bosses.size()==2) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)>greaterthanvalue&&i+1<nobosses.size()&&nobosses.get(i)==nobosses.get(i+1)) {
					return true;
				}
			}
		} else if (bosses.size()==1) {
			return has3same(nobosses, greaterthanvalue);
		} else if (bosses.size()==0) {
			for (int i=0;i<nobosses.size();i++) {
				if (nobosses.get(i)>greaterthanvalue&&i+3<nobosses.size()&&
						nobosses.get(i)==nobosses.get(i+1)&&nobosses.get(i+1)==nobosses.get(i+2)&&
						nobosses.get(i+2)==nobosses.get(i+3)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean hasconsequence(List<Integer> cards, Integer equalsvalue, Integer numofcards){
		if (cards.size()<numofcards) {
			return false;
		}
		int bosses = 0;
		List<Integer> nobosses = new ArrayList<Integer>();
		for (Integer i:cards) {
			if (i==14||i==15) {
				bosses++;
			} else {
				nobosses.add(i);
			}
		}
		if (equalsvalue==14&&bosses==2) {
			return true;
		}
		for (int i = 0; i<numofcards;i++) {
			if (equalsvalue+i==13) {
				return false;
			}
			if (!nobosses.contains(equalsvalue+i)) {
				bosses--;
				if (bosses<0) {
					return false;
				}
			}
		}
		return true;
	}

	
	public synchronized Integer in(String playerid, Incard incard) throws Exception {
		refreshtime = System.currentTimeMillis();
		if (started==0) {
			throw new Exception("notstarted");
		}
		if (getplayer(playerid).index!=nextplayerindex){
			throw new Exception("notyourturn");
		}
		autoin = 40;
		Integer returnvalue = 0;
		Player p = getplayer(playerid);
		if (incard!=null) {
			if (ok(incard)) {
				p.out(incard);
				p.noplay=1;
				incards.add(incard);
				for (Integer cardval:incard.cardvalues) {
					morecards.add(cardval);
				}
				if (incards.size()>2) {
					incards.remove(0);
				}
				if (p.mycards.isEmpty()) {
					losts(true);
					Longpollnotify.donotify(this, Longpollnotify.ACTION_INCARD, null);
					return 1;
				} else {
					Integer rest = 0;
					for (Player player: players) {
						rest += player.mycards.size();
					}
					if (rest==players.size()) {
						losts(false);
						Longpollnotify.donotify(this, Longpollnotify.ACTION_INCARD, null);
						return 1;
					}
				}
				returnvalue = 1;
			} else {
				p.prevaction=0;
			}
		} else {
			p.prevaction=0;
		}
		if (started==1) {
			autopass();
			if (!incards.isEmpty()&&incards.get(incards.size()-1).playerid().equals(players.get(nextplayerindex).id())&&players.get(nextplayerindex).prevaction!=0) {
				if (settingallget==0) {
					if (!allcards.isEmpty()) {
						players.get(nextplayerindex).in(allcards.remove(new Random().nextInt(allcards.size())));
						restcards = allcards.size();
					} else if (!morecards.isEmpty()) {
						players.get(nextplayerindex).in(morecards.remove(new Random().nextInt(morecards.size())));
					}
				} else {
					for (Player ps:players) {
						if (!allcards.isEmpty()) {
							ps.in(allcards.remove(new Random().nextInt(allcards.size())));
							restcards = allcards.size();
						} else if (!morecards.isEmpty()) {
							ps.in(morecards.remove(new Random().nextInt(morecards.size())));
						}
					}
				}
			}
		}
		Longpollnotify.donotify(this, Longpollnotify.ACTION_INCARD, null);
		return returnvalue;
	}
	private boolean ok(Incard incard) {
		if (incards.isEmpty()||incards.get(incards.size()-1).playerid().equals(players.get(nextplayerindex).id())) {
			return true;
		}
		if (!incards.isEmpty()&&incards.get(incards.size()-1).twoboss==1) {
			return false;
		}
		if (incard.twoboss==1) {
			times=times*4;
			return true;
		}
		if (incards.get(incards.size()-1).cardvalues.size()==1) {
			if (incard.type==0&&incard.cardvalues.size()>2) {
				if (incard.cardvalues.size()==4) {
					times=times*4;
				} else {
					times = times*2;
				}
				return true;
			} else if (incard.cardvalues.size()==1&&(incard.minplayervalue==incards.get(incards.size()-1).minplayervalue+1||incards.get(incards.size()-1).minplayervalue!=13&&incard.minplayervalue==13)) {
				return true;
			}
		} else {
			if (incards.get(incards.size()-1).type==0&&incards.get(incards.size()-1).cardvalues.size()==2||
					incards.get(incards.size()-1).type==1) {
				if (incard.type==0&&incard.cardvalues.size()>2) {
					if (incard.cardvalues.size()==4) {
						times=times*4;
					} else {
						times = times*2;
					}
					return true;
				} else if (incard.cardvalues.size()==incards.get(incards.size()-1).cardvalues.size()&&
						incard.cardvalues.size()>=2&&(incard.minplayervalue==incards.get(incards.size()-1).minplayervalue+1||incards.get(incards.size()-1).minplayervalue!=13&&incard.minplayervalue==13)) {
					return true;
				}
			} else {
				if (incard.type==0&&incard.cardvalues.size()>2) {
					if (incard.cardvalues.size()>incards.get(incards.size()-1).cardvalues.size()) {
						if (incard.cardvalues.size()==4) {
							times=times*4;
						} else {
							times = times*2;
						}
						return true;
					}
					if (incard.cardvalues.size()==incards.get(incards.size()-1).cardvalues.size()&&
							incard.minplayervalue>incards.get(incards.size()-1).minplayervalue) {
						if (incard.cardvalues.size()==4) {
							times=times*4;
						} else {
							times = times*2;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	private void losts(boolean sum) {
		for (Player p:players) {
			if (sum&&p.mycards.size()>1) {
				p.losts+=p.mycards.size()*times*p.noplay;
			}
		}
		times=1;
		started = 0;
		morecards.clear();
		nextplayerindex = round%players.size();
	}
}
