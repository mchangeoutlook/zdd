package com.zdd.bdc.client.sort;

import java.nio.file.Path;

public interface Sortoutput {
	public void output(long position, String key, long value, Path sortingfolder) throws Exception;
}
