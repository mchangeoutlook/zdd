package com.zdd.bdc.sort.local;

import java.nio.file.Path;

public interface Sortoutput {
	public void init(Path sortingfolder) throws Exception;
	public void output(long position, String key, long value) throws Exception;
}
