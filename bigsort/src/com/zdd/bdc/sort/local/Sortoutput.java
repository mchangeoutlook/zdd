package com.zdd.bdc.sort.local;

import java.nio.file.Path;

public interface Sortoutput {
	public void output(long position, String key, long value, Path sortingfolder) throws Exception;
}
