package com.tenotenm.gdy.util;

import java.util.Hashtable;
import java.util.Map;

import com.tenotenm.gdy.game.Judge;

public class Judges {
	private static final Map<String, Judge> judges = new Hashtable<String, Judge>();
	public synchronized static final void put(String judgeid, Judge judge) {
		judges.put(judgeid, judge);
	}
	public static final Judge get(String judgeid) {
		return judges.get(judgeid);
	}
	
}
