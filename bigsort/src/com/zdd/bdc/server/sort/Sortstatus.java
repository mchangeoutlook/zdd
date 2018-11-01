package com.zdd.bdc.server.sort;

import java.nio.file.Path;

public interface Sortstatus {
	
	public static final int NOTFOUND = 0;
	public static final int SORTING = 1;
	public static final int READY = 2;
	
	public int status(Path sortingfolder);
}
