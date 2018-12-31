package com.zdd.bdc.server.util;

import java.nio.file.Path;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Fileconfigutil {

	public static void create(String key, String value, String namespace, String configfile) throws Exception {
		Filekvutil.create(key, value, 500, file(namespace, configfile));
	}
	public static void modify(String key, String newvalue, String namespace, String configfile) throws Exception {
		Filekvutil.modifyvaluebykey(key, newvalue,
				file(namespace, configfile));
	}
	public static String readone(String key, String namespace, String configfile) throws Exception {
		String returnvalue = Filekvutil.readvaluebykey(key, file(namespace, configfile));
		if (STATIC.NAMESPACE_CORE.equals(namespace)&&STATIC.REMOTE_CONFIG_PENDING.equals(configfile)) {
			modify(key,"",namespace,configfile);
		} else {
			//do nothing
		}
		return returnvalue;
	}

	public static Vector<String> readmany(String key, String namespace, String configfile) throws Exception {
		Vector<String> returnvalue = Filekvutil.readallvaluesbykey(key, 
				file(namespace, configfile));
		return returnvalue;
	}

	public static Path file(String namespace, String configfile) {
		return STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
	}
}
