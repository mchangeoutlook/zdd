package com.zdd.bdc.util;

public class Filedatawalkresult {
	private int walkaction = -1;
	private int dataction = -1;

	private byte[] value1 = null;
	private byte[] value2 = null;

	public Filedatawalkresult(int thewalkaction, int thedataction, byte[] v1, byte[] v2) {
		walkaction = thewalkaction;
		dataction = thedataction;
		value1 = v1;
		value2 = v2;
	}

	public byte[] getvalue1() {
		return value1;
	}

	public byte[] getvalue2() {
		return value2;
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
