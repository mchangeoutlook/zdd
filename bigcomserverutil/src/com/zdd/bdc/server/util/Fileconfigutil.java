package com.zdd.bdc.server.util;

import java.nio.file.Path;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Fileconfigutil {

	public static void configcreate(String key, String value, String namespace, String configfile) throws Exception {
		Filekvutil.create(key, value, 500, configfile(namespace, configfile));
	}
	public static void configmodify(String key, String newvalue, String namespace, String configfile) throws Exception {
		Filekvutil.modifyvaluebykey(key, newvalue,
				configfile(namespace, configfile));
	}
	public static String config(String key, String namespace, String configfile) throws Exception {
		String returnvalue = Filekvutil.readvaluebykey(key, configfile(namespace, configfile));
		if (STATIC.NAMESPACE_CORE.equals(namespace)&&STATIC.REMOTE_CONFIG_PENDING.equals(configfile)) {
			configmodify(key,"",namespace,configfile);
		} else {
			//do nothing
		}
		return returnvalue;
	}

	public static Vector<String> configs(String key, String namespace, String configfile) throws Exception {
		Vector<String> returnvalue = Filekvutil.readallvaluesbykey(key, 
				configfile(namespace, configfile));
		return returnvalue;
	}

	public static Path configfile(String namespace, String configfile) {
		return STATIC.LOCAL_CONFIGFOLDER.resolve(namespace).resolve(configfile);
	}
}
