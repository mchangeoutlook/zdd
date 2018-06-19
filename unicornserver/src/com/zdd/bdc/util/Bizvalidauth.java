package com.zdd.bdc.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdd.bdc.biz.Indexclient;
import com.zdd.bdc.biz.Textclient;

public class Bizvalidauth {

	public static void go(Bizparams bizp, String requesturl, HttpServletResponse response) throws IOException {
		Map<String, Object> returnvalue = new HashMap<String, Object>();
		try {
			Ibiz biz = (Ibiz) Class.forName("com.zdd.bdc.biz."+requesturl.substring(requesturl.lastIndexOf("/")+1)).getDeclaredConstructor().newInstance();
			Map<String, String> validresult = Bizvalidauth.valid(biz.validrules(), bizp);
			if (validresult.isEmpty()) {
				String resourceid = biz.auth(bizp);
				String actioncode = biz.getClass().getSimpleName();
				if (resourceid!=null&&actioncode!=null&&!Bizvalidauth.auth(resourceid, bizp, actioncode)) {
					returnvalue.put("state", 3);
					returnvalue.put("reason", "denied");
				}
			} else {
				returnvalue.put("state", 2);
				returnvalue.put("reason", validresult);
			}
			if (returnvalue.isEmpty()) {
				returnvalue.put("state", 0);
				Map<String, Object> ret =  biz.process(bizp);
				if (ret!=null) {
					returnvalue.put("data", ret);
				} else {
					returnvalue.put("data", new Hashtable<String, Object>());
				}
			}
		} catch (Exception e) {
			returnvalue.put("state", 1);
			returnvalue.put("reason", e.getMessage());
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			returnvalue.put("detail", errors.toString());
		} finally {
			bizp.clear();
		}
		response.getWriter().print(new ObjectMapper().writeValueAsString(returnvalue));
	}
	
	private static Map<String, String> valid(Map<String, String> paramrules, Bizparams bizp) {
		int params = 0;
		if (paramrules != null) {
			params = paramrules.size();
		}
		Map<String, String> returnvalue = new Hashtable<String, String>(params);
		if (params>0) {
			for (String key:paramrules.keySet()) {
				if (Ibiz.VALIDRULE_NOTEMPTY.equals(paramrules.get(key))) {
					if (bizp.getfile(key)==null&&bizp.getext(key)==null||
							bizp.getfile(key)==null&&bizp.getext(key)!=null&&bizp.getext(key).trim().isEmpty()) {
						returnvalue.put(key, paramrules.get(key));
					}
				} else if (paramrules.get(key)!=null&&paramrules.get(key).startsWith(Ibiz.VALIDRULE_MIN_MAX_PREFIX)) {
					String[] minmax = paramrules.get(key).substring(Ibiz.VALIDRULE_MIN_MAX_PREFIX.length()).split(Ibiz.SPLITTER);
					if (Long.parseLong(bizp.getext(key))<Long.parseLong(minmax[0])||Long.parseLong(bizp.getext(key))>Long.parseLong(minmax[1])) {
						returnvalue.put(key, paramrules.get(key));
					}
				} else  if (paramrules.get(key)!=null&&paramrules.get(key).startsWith(Ibiz.VALIDRULE_REGEX_PREFIX)) {
					Pattern regex = Pattern.compile(paramrules.get(key).substring(Ibiz.VALIDRULE_REGEX_PREFIX.length()));
					if (!regex.matcher(bizp.getext(key)).matches()) {
						returnvalue.put(key, paramrules.get(key));
					}
				}
			}
		}
		return returnvalue;
	}
	
	private static boolean auth(String resourceid, Bizparams bizp, String actioncode) throws Exception {
		String authcode = Textclient.getinstance("unicorn", "auth").key(Indexclient.getinstance("unicorn", bizp.getaccountkey()+Ibiz.SPLITTER+resourceid).filters(1).add(actioncode).readunique()).columns(1).add("code").read().get("code");
		if (actioncode.equals(authcode)) {
			return true;
		}
		return false;
	}
}
