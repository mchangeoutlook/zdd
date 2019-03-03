package com.zdd.bdc.server.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Fileconfigutil {

	private static final String multivalues="$multivalues:$";
	
	public static synchronized void create(String key, String value, String namespace, String configfile) throws Exception {
		Properties p = new Properties();
		Properties pmultivalues = new Properties();
		
		Path target = file(namespace, configfile);
		
		InputStream is = null;
		InputStream ismultivalues = null;
		
		OutputStream os = null;
		OutputStream osmultivalues = null;
		
		try {
			if (Files.exists(target)) {
				is = Files.newInputStream(target);
				p.load(is);
			}
			if (p.getProperty(key)==null) {
				p.put(key, value);
			} else {
				Path targetmultivalues = file(namespace, configfile+"."+key);
				if (p.getProperty(key).equalsIgnoreCase(multivalues)) {
					ismultivalues = Files.newInputStream(targetmultivalues);
					pmultivalues.load(ismultivalues);
					pmultivalues.put(String.valueOf(pmultivalues.size()), value);
				} else {
					pmultivalues.put(String.valueOf(pmultivalues.size()), p.getProperty(key));
					pmultivalues.put(String.valueOf(pmultivalues.size()), value);
					p.put(key, multivalues);
				}
				synchronized(Fileutil.syncfile(targetmultivalues)){
					osmultivalues = Files.newOutputStream(targetmultivalues);
					pmultivalues.store(osmultivalues, "Multivalues of key ["+key+"] in configfile ["+configfile+"]");
				}
			}
			
			synchronized(Fileutil.syncfile(target)){
				os = Files.newOutputStream(target);
				p.store(os, "Properties of "+configfile);
			}
			
		}finally {
			if (is!=null) {
				is.close();
			}
			if (os !=null ) {
				os.close();
			}
			if (ismultivalues!=null) {
				ismultivalues.close();
			}
			if (osmultivalues !=null ) {
				osmultivalues.close();
			}
		}
		
	}
	
	private static Vector<String> read(String key, String namespace, String configfile) throws Exception{
		Properties p = new Properties();
		Properties pmultivalues = new Properties();
		
		Path target = file(namespace, configfile);
		
		InputStream is = null;
		InputStream ismultivalues = null;
		
		OutputStream os = null;
		
		try {
			if (Files.exists(target)) {
				is = Files.newInputStream(target);
				p.load(is);
			}
			if (p.getProperty(key)==null) {
				return null;
			} else {
				Path targetmultivalues = file(namespace, configfile+"."+key);
				if (p.getProperty(key).equalsIgnoreCase(multivalues)) {
					ismultivalues = Files.newInputStream(targetmultivalues);
					pmultivalues.load(ismultivalues);
					Vector<String> ret = new Vector<String>(pmultivalues.size());
					int i = 0;
					while(pmultivalues.getProperty(String.valueOf(i))!=null) {
						ret.add(pmultivalues.getProperty(String.valueOf(i++)));
					}
					return ret;
				} else {
					Vector<String> ret = new Vector<String>(1);
					ret.add(p.getProperty(key));
					return ret;
				}
			}
		}finally {
			if (STATIC.NAMESPACE_CORE.equals(namespace)&&STATIC.REMOTE_CONFIG_PENDING.equals(configfile)&&
					STATIC.REMOTE_CONFIGVAL_PENDING.equals(p.getProperty(key))) {
				synchronized(Fileutil.syncfile(target)){
					os = Files.newOutputStream(target);
					p.put(key, "");
					p.store(os, "Properties of "+configfile);
				}
			}
			if (is!=null) {
				is.close();
			}
			if (os !=null ) {
				os.close();
			}
			if (ismultivalues!=null) {
				ismultivalues.close();
			}
		}
	}
	
	public static String readone(String key, String namespace, String configfile) throws Exception {
		Vector<String> returnvalue = read(key, namespace, configfile);
		if (returnvalue!=null&&!returnvalue.isEmpty()) {
			return returnvalue.get(0);
		}
		return null;
	}

	public static Vector<String> readmany(String key, String namespace, String configfile) throws Exception {
		return read(key, namespace, configfile);
	}

	public static Path file(String namespace, String configfile) throws Exception {
		Path folder = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace);
		if (!Files.exists(folder)) {
			Files.createDirectories(folder);
		}
		return folder.resolve(configfile);
	}
	
}
