package com.zdd.bdc.sort.local;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

public class Sortcontrol {
	public static final int CONTINUE = 0;
	public static final int TERMINATE = 1;
	
	private static Map<String, Integer> SC = new Hashtable<String, Integer>();
	
	public static void to(Path sortingfolder, int status) {
		SC.put(sortingfolder.toString(), status);
	}
	
	public static int to(Path sortingfolder) {
		return SC.get(sortingfolder.toString());
	}
	
}
