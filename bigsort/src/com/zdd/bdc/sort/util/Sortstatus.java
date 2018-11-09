package com.zdd.bdc.sort.util;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

public class Sortstatus {
	public static final String SORT_NOTINCLUDED = "sort_notincluded";
	public static final String SORT_INCLUDED = "sort_included";
	
	public static final String SORTING_CONTINUE = "sorting_continue";
	public static final String SORTING_TERMINATE = "sorting_terminate";
	public static final String SORTING_ACCOMPLISHED = "sorting_accomplished";

	public static final String DISTRIBUTE_CONTINUE = "distr_continue";
	public static final String DISTRIBUTE_TERMINATE = "distr_terminate";
	public static final String DISTRIBUTE_ACCOMPLISHED = "distr_accomplished";
	
	private static final Map<Path, String> STATUS = new Hashtable<Path, String>();
	
	public static String get(Path sortingfolder) {
		return STATUS.get(sortingfolder);
	}
	public static void set(Path sortingfolder, String status) {
		STATUS.put(sortingfolder, status);
	}
}
