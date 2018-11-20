package com.zdd.bdc.sort.distribute;

import java.nio.file.Path;
import java.util.Map;

public interface Sortcheck {
	public String check(Path sortingfolder, Map<String, String> additionalconfigs) throws Exception;
}
