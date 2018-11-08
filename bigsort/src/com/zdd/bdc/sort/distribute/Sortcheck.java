package com.zdd.bdc.sort.distribute;

import java.nio.file.Path;

public interface Sortcheck {
	
	public static final String SORT_NOTINCLUDED = "notincluded";
	public static final String SORT_INCLUDED = "included";
	
	public String check(Path sortingfolder);
}
