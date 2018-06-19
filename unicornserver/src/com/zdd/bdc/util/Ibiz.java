package com.zdd.bdc.util;

import java.util.Map;

public interface Ibiz {
	public static final String SPLITTER = "#";
	
	public static final String VALIDRULE_NOTEMPTY = "notempty";
	public static final String VALIDRULE_MIN_MAX_PREFIX = "minmax"+SPLITTER;
	public static final String VALIDRULE_REGEX_PREFIX = "regex"+SPLITTER;
		
	public Map<String, String> validrules();	
	public String auth(Bizparams bizp) throws Exception;
	public Map<String, Object> process(Bizparams bizp) throws Exception;
}
