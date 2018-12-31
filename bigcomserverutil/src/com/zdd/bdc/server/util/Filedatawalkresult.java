package com.zdd.bdc.server.util;

public class Filedatawalkresult {
	private int walkaction = -1;
	private int dataction = -1;

	private String newvalue = null;

	public Filedatawalkresult(int thewalkaction, int thedataction, String thenewvalue) {
		walkaction = thewalkaction;
		dataction = thedataction;
		newvalue = thenewvalue;
	}

	public String getnewvalue() {
		if (newvalue == null) {
			return "";
		}
		return newvalue;
	}
	
	public int getwalkaction() {
		return walkaction;
	}

	public int getdataction() {
		return dataction;
	}

	public static final int WALK_CONTINUE = 0;
	public static final int WALK_TERMINATE = 1;

	public static final int DATA_DELETE = 0;
	public static final int DATA_REPLACE = 1;
	public static final int DATA_DONOTHING = -1;

}
