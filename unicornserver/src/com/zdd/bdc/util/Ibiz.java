package com.zdd.bdc.util;

import java.util.Map;

public interface Ibiz {
	public static final String VALIDRULE_NOTEMPTY = "notempty";
	public static final String VALIDRULE_MIN_MAX_PREFIX = "minmax";
	public static final String VALIDRULE_REGEX_PREFIX = "regex";
	
	public Map<String, String> validrules();	
	public String auth(Bizparams bizp);
	public Map<String, Object> process();
}
