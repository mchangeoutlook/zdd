package com.zdd.biz.game10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.util.Longpoll;

public class Longpollnotify {
	//value-> 0:nothing; 1:notify;
	//key-> playerid_join; playerid_dispatch; playerid_incard;;
	public static final String ACTION_JOIN = "_join";
	public static final String ACTION_DISPATCH = "_dispatch";
	public static final String ACTION_INCARD = "_incard";
	private static final Map<String,Integer> playerid_action_notify = new HashMap<String, Integer>();

	private static final Map<String, String> playeridnotifytime = new HashMap<String, String>();
	
	public static synchronized void donotify(Judge j, String action, String playerid) throws Exception {
		List<Player> notifyplayers = new ArrayList<Player>();
		if (action==null&&j.getplayer(playerid)!=null) {
			notifyplayers.add(j.getplayer(playerid));
		} else {
			notifyplayers = j.players();
		}
		
		for (Player pp:notifyplayers) {
			if (action!=null) {
				playerid_action_notify.put(pp.id()+action, 1);
			}
			if (playerid_action_notify.get(pp.id()+ACTION_JOIN)!=null&&playerid_action_notify.get(pp.id()+ACTION_JOIN)==0&&
					playerid_action_notify.get(pp.id()+ACTION_DISPATCH)!=null&&playerid_action_notify.get(pp.id()+ACTION_DISPATCH)==0&&
									playerid_action_notify.get(pp.id()+ACTION_INCARD)!=null&&playerid_action_notify.get(pp.id()+ACTION_INCARD)==0) {
				continue;
			}
			List<Map<String, Object>> players = new ArrayList<Map<String, Object>>();
			for (Player p:j.players()) {
				Map<String, Object> player = new HashMap<String, Object>();
				player.put("index", p.index);
				if (pp.id().equals(p.id())) {
					player.put("meindex", p.index);
				}
				if (p.index==j.nextplayerindex) {
					player.put("meplay", p.index);	
				}
				player.put("wins", p.wins);
				
				if (playerid_action_notify.get(pp.id()+ACTION_JOIN)!=null&&playerid_action_notify.get(pp.id()+ACTION_JOIN)==1) {
					player.put("nick", p.nick);
				}
				
				if (playerid_action_notify.get(pp.id()+ACTION_DISPATCH)!=null&&playerid_action_notify.get(pp.id()+ACTION_DISPATCH)==1||
						playerid_action_notify.get(pp.id()+ACTION_INCARD)!=null&&playerid_action_notify.get(pp.id()+ACTION_INCARD)==1) {
					if (pp.id().equals(p.id())) {
						player.put("cards", p.mycards);
					}
				}
				players.add(player);
			}
			
			Map<String, Object> tonotify = new HashMap<String, Object>();
			tonotify.put("status", "yes");
			tonotify.put("judge", j);
			tonotify.put("players", players);
			tonotify.put("notifytime", String.valueOf(System.currentTimeMillis()));
			if (Longpoll.end(pp.id(), new ObjectMapper().writeValueAsString(tonotify))) {
				playeridnotifytime.put(pp.id()+"_notifytime",tonotify.get("notifytime").toString());
			}
		}

	}
	
	public static final void clear(String playerid, String notifytime) {
		if (notifytime!=null&&notifytime.equals(playeridnotifytime.get(playerid+"_notifytime"))) {
			playerid_action_notify.put(playerid+ACTION_JOIN, 0);
			playerid_action_notify.put(playerid+ACTION_DISPATCH, 0);
			playerid_action_notify.put(playerid+ACTION_INCARD, 0);
		}
	}
}
