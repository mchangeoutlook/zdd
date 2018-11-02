package com.zdd.bdc.client.sort;

import java.nio.file.Path;

public interface Clientstatus {
	
	public static final int NOTFOUND = 0;
	public static final int SORTING = 1;
	public static final int MERGED = 2;
	
	public int status(Path sortingfolder);
}
