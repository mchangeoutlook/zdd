package com.zdd.bdc.server.sort;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

public class Serverstatus {
	public static final int PROGRESS = 0;
	public static final int ERROR = 1;
	public static final int SUCCESS = 2;
	
	private static Map<String, Integer> ST = new Hashtable<String, Integer>();
	
	public static void status(Path sortingfolder, int status) {
		ST.put(sortingfolder.toString(), status);
	}
	
	public static int status(Path sortingfolder) {
		return ST.get(sortingfolder.toString());
	}
	
}
