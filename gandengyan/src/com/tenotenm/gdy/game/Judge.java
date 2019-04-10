package com.tenotenm.gdy.game;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.tenotenm.gdy.util.Common;
import com.tenotenm.gdy.util.Judges;

//3=3,4=4,5=5,6=6,7=7,8=8,9=9,10=10,11=J,12=Q,13=K,14=A,15=2,16=👑

public class Judge {
	public static final int maxnumofseats = 9;
	public static final int maxnumofcards = 6;
	public static final int mincardvalue = 3;
	public static final int maxcardvalue = 16;
	
	private long times = 1;
	private String gameid = null;
	private long round = -1;
	private String playeridtoserve = null;//庄家
	private String playeridtoplay = null;//出牌
	private Map<Integer, Player> seatno_players = new Hashtable<Integer, Player>();
	private Map<String, Player> playerid_players = new Hashtable<String, Player>();

	public synchronized String creategame() {
		if (gameid == null) {
			gameid = Common.generateid();
			initcurrentround();
			Judges.put(gameid, this);
		}
		return gameid;
	}

	public Player getplayerbyid(String playerid) {
		return playerid_players.get(playerid);
	}
	
	public Player getplayerbyseat(int seatno) {
		return seatno_players.get(seatno);
	}

	public String actname(String playerid, String newname) {
		getplayerbyid(playerid).setname(newname);
		pushstatus_name(playerid);
		return getplayerbyid(playerid).getname();
	}
	
	private synchronized void initcurrentround() {
		if (round_statusqueue.get(round)==null) {
			round_statusqueue.put(round, new Vector<Map<String, Object>>());
		}
		round_statusqueue.remove(round-2);//keep last 2 rounds of status in queue to avoid concurrent reading error.
	}
	
	public synchronized String takeseat_startnextround(Integer seatno) throws Exception {
		if (seatno==null) {// starting next round
			round++;
			initcurrentround();
			
			return null;
		} else {//taking seat
			if (seatno<0||seatno>=maxnumofseats) {
				throw new Exception("这个座位适合观战");
			}
			if (seatno >= 0 && round >= 0) {
				throw new Exception("他们没等你就开始了，不能占座了，观战吧");
			}
			if (seatno < 0 && round >= 0) {
				throw new Exception("激战正酣，观战吧");
			}
			if (seatno_players.get(seatno)!=null) {
				throw new Exception("座位被占了，试试别的座位吧");
			}
			initcurrentround();
			seatno_players.put(seatno, new Player(seatno));
			pushstatus_takeseat(seatno);
			return seatno_players.get(seatno).getid();
		}
	}

	public Map<String, Object> querysnapshot(String theplayerid){
		Map<String, Object> s = new Hashtable<String, Object>();
		s.put("status", "snapshot");
		s.put("round", round);
		s.put("times", times);
		int queueindex = round_statusqueue.get(s.get("round")).size()-1;//read the last action status through querystatus(...)
		if (queueindex<0) {
			queueindex = 0;
		}
		s.put("queueindex", queueindex);
		Vector<Map<String, Object>> players = new Vector<Map<String, Object>>(9);
		for (String playerid:playerid_players.keySet()) {
			Map<String, Object> p = new Hashtable<String, Object>();
			p.put("name", playerid_players.get(playerid).getname());
			p.put("score", playerid_players.get(playerid).getscore());
			p.put("seatno", playerid_players.get(playerid).getseatno());
			players.add(p);
			if (playerid.equals(theplayerid)) {
				s.put("cardsinhand", playerid_players.get(playerid).cardsinhand());
			}
			if (playerid.equals(playeridtoserve)) {
				s.put("seatnotoserve", p.get("seatno"));
			}
		}
		s.put("players", players);
		return s;
	}

	private Map<Long, Vector<Map<String, Object>>> round_statusqueue = new Hashtable<Long, Vector<Map<String, Object>>>();
	
	public Vector<Map<String, Object>> querystatus(String playerid, long theround, int queueindex){
		Vector<Map<String, Object>> ret = new Vector<Map<String, Object>>();
		for (int i = queueindex;i<round_statusqueue.get(theround).size();i++) {
			ret.add(round_statusqueue.get(theround).get(i));
		}
		return ret;
	}
	
	public void pushstatus_takeseat(int seatno) {
		Map<String, Object> s = new Hashtable<String, Object>();
		s.put("status", "takeseat");
		s.put("seatno", seatno);
		round_statusqueue.get(round).add(s);
	}

	public void pushstatus_name(String playerid) {
		Map<String, Object> s = new Hashtable<String, Object>();
		s.put("status", "name");
		s.put("seatno", getplayerbyid(playerid).getseatno());
		s.put("playername", getplayerbyid(playerid).getname());
		round_statusqueue.get(round).add(s);
	}

	public void pushstatus_serve() {

	}

	public void pushstatus_play() {

	}

	public void pushstatus_roundover() {

	}
	
}
