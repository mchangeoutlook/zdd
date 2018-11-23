package com.zdd.bdc.sort.util;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;

public class Sortstatus {
	public static final String SORT_NOTINCLUDED = "sort_notincluded";
	public static final String SORT_INCLUDED = "sort_included";
	public static final String READY_TO_DISTRIBUTE = "ready_to_distribute";
	public static final String ACCOMPLISHED = "distribute_accomplished";
	public static final String TERMINATE = "terminate";
	
	private static final Map<Path, String> STATUS = new Hashtable<Path, String>();
	
	public static String get(Path sortingfolder) {
		return STATUS.get(sortingfolder);
	}
	public static void set(Path sortingfolder, String status) throws Exception {
		synchronized(STATIC.syncfile(sortingfolder)) {
			if (!TERMINATE.equals(status)&&TERMINATE.equals(get(sortingfolder))) {
				throw new Exception("terminate");
			} else if (READY_TO_DISTRIBUTE.equals(get(sortingfolder))&&SORT_INCLUDED.equals(status)){
				//do nothing
			} else {
				STATUS.put(sortingfolder, status);
			}
		}
	}
}
