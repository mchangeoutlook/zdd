package com.zdd.bdc.util;

import java.util.Map;

public interface Ibiz {
	public static final String SPLITTER = "#";
	
	public static final String VALIDRULE_NOTEMPTY = "notempty";
	public static final String VALIDRULE_MIN_MAX_PREFIX = "minmax"+SPLITTER;
	public static final String VALIDRULE_REGEX_PREFIX = "regex"+SPLITTER;
	
	public static final String ACTIONCODE_CREATE = "1000";
	public static final String ACTIONCODE_DELETE = "0100";
	public static final String ACTIONCODE_MODIFY = "0010";
	public static final String ACTIONCODE_READ = "0001";
	public static final String ACTIONCODE_NONE = "0000";
	
	public Map<String, String> validrules();	
	public String auth(Bizparams bizp) throws Exception;
	public String actioncode();
	public Map<String, Object> process(Bizparams bizp) throws Exception;
}
