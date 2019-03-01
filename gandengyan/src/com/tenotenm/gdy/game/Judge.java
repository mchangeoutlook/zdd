package com.tenotenm.gdy.game;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.tenotenm.gdy.util.Common;
import com.tenotenm.gdy.util.Judges;

//3=3,4=4,5=5,6=6,7=7,8=8,9=9,10=10,11=J,12=Q,13=K,14=A,15=2,16=👑

public class Judge {
	public static final int maxnumofseats = 9;
	public static final int maxnumofcards = 6;
	public static final int mincardvalue = 3;
	public static final int maxcardvalue = 16;
	
	private String gameid = null;
	private long round = -1;
	private String turnplayerid = null;
	private Map<Integer, Player> seatno_players = new Hashtable<Integer, Player>();
	private Map<String, Player> playerid_players = new Hashtable<String, Player>();

	public synchronized String creategame() {
		if (gameid == null) {
			gameid = Common.generateid();
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

	public String rename(String playerid, String newname) {
		getplayerbyid(playerid).setname(newname);
		pushstatus_name(playerid);
		return getplayerbyid(playerid).getname();
	}
	
	public synchronized String joinorstartgame(int seatno) throws Exception {
		if (seatno<0||seatno>=maxnumofseats) {
			throw new Exception("这个座位适合观战");
		}
		if (seatno >= 0 && round >= 0) {
			throw new Exception("他们没等你就开始了，不能占座了，观战吧");
		}
		if (seatno < 0 && round >= 0) {
			throw new Exception("激战正酣，观战吧");
		}
		if (seatno >= 0) {// taking seat
			if (seatno_players.get(seatno)!=null) {
				throw new Exception("座位被占了，试试别的座位吧");
			}
			seatno_players.put(seatno, new Player(seatno));
			pushstatus_takeseat(seatno);
			return seatno_players.get(seatno).getid();
		} else {// starting game
			round = 0;
			turnplayerid = "";
			return null;
		}
	}

	public Map<String, Object> querystatus_snapshot(String playerid){
		
		return null;
	}

	private Map<Long, Vector<Map<String, Object>>> round_statusqueue = new Hashtable<Long, Vector<Map<String, Object>>>();
	
	public Vector<Map<String, Object>> querystatus(String playerid, int queueindex){
		
		return null;
	}
	
	public void pushstatus_takeseat(int seatno) {
		Map<String, Object> s = new Hashtable<String, Object>();
		s.put("status", "takeseat");
		s.put("seatno", seatno);
		s.put("playername", getplayerbyseat(seatno).getname());
		s.put("playernumofcardsinhand", getplayerbyseat(seatno).cardsinhand().size());
		s.put("playerscore", getplayerbyseat(seatno).getscore());
		round_statusqueue.get(round).add(s);
	}

	public void pushstatus_name(String playerid) {
		Map<String, Object> s = new Hashtable<String, Object>();
		s.put("status", "name");
		s.put("seatno", getplayerbyid(playerid).getseatno());
		s.put("playername", getplayerbyid(playerid).getname());
		round_statusqueue.get(round).add(s);
	}

	public void pushstatus_served() {

	}

	public void pushstatus_roundover() {

	}
	
}
