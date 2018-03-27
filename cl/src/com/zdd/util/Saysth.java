package com.zdd.util;

public class Saysth {
	public static String sayhtml(String str, Integer maxlength) {
		if (str.length()>maxlength) {
			str = str.substring(0,maxlength);
		}
		str=str.replaceAll("<", "&lt;");
		str=str.replaceAll(">", "&gt;");
		return saygood(str);
	}
	public static String saygood(String str) {
		if (str.toLowerCase().contains("tmd")||str.toLowerCase().contains("fuck")||str.toLowerCase().contains("ass")||
				str.contains("操")||str.contains("傻")||str.contains("妈")&&!str.contains("妈妈")||str.contains("狗")||str.contains("狱")||
				str.contains("屎")||str.contains("蛋")||str.contains("猪")||str.contains("病")||str.contains("鸡")||str.contains("尿")||str.contains("死")) {
			return "应该多读点书";
		}
		return str.trim();
	}
}
