package com.zdd.bdc.server.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;

public class Fileconfigutil {

	private static final String multivalues = "$multivalues:$";

	public static synchronized void create(String key, String value, String namespace, String configfile)
			throws Exception {
		Properties p = new Properties();
		Properties pmultivalues = new Properties();

		Path target = file(namespace, configfile, true);

		InputStream is = null;
		InputStream ismultivalues = null;

		OutputStream os = null;
		OutputStream osmultivalues = null;

		try {
			if (Files.exists(target)) {
				is = Files.newInputStream(target);
				p.load(is);
			}
			if (p.getProperty(key) == null) {
				p.put(key, value);
			} else {
				Path targetmultivalues = file(namespace, configfile + "." + key, true);
				if (p.getProperty(key).equalsIgnoreCase(multivalues)) {
					ismultivalues = Files.newInputStream(targetmultivalues);
					pmultivalues.load(ismultivalues);
					pmultivalues.put(String.valueOf(pmultivalues.size()), value);
				} else {
					pmultivalues.put(String.valueOf(pmultivalues.size()), p.getProperty(key));
					pmultivalues.put(String.valueOf(pmultivalues.size()), value);
					p.put(key, multivalues);
				}
				synchronized (Fileutil.syncfile(targetmultivalues)) {
					osmultivalues = Files.newOutputStream(targetmultivalues);
					pmultivalues.store(osmultivalues,
							"Multivalues of key [" + key + "] in configfile [" + configfile + "]");
				}
			}

			synchronized (Fileutil.syncfile(target)) {
				try {
					os = Files.newOutputStream(target);
					p.store(os, "Properties of " + configfile);
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (Exception e) {
							// do nothing
						}
					}
				}
			}

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// do nothing
				}
			}

			if (ismultivalues != null) {
				try {
					ismultivalues.close();
				} catch (Exception e) {
					// do nothing
				}
			}
			if (osmultivalues != null) {
				try {
					osmultivalues.close();
				} catch (Exception e) {
					// do nothing
				}
			}
		}

	}

	private static Vector<String> read(String key, String namespace, String configfile) throws Exception {
		Properties p = new Properties();
		Properties pmultivalues = new Properties();

		Path target = file(namespace, configfile, false);
		if (!Files.exists(target.getParent())) {
			return null;
		}
		InputStream is = null;
		InputStream ismultivalues = null;

		OutputStream os = null;

		try {
			if (Files.exists(target)) {
				is = Files.newInputStream(target);
				p.load(is);
			}
			if (p.getProperty(key) == null) {
				return null;
			} else {
				if (p.getProperty(key).equalsIgnoreCase(multivalues)) {
					ismultivalues = Files.newInputStream(file(namespace, configfile + "." + key, false));
					pmultivalues.load(ismultivalues);
					Vector<String> ret = new Vector<String>(pmultivalues.size());
					int i = 0;
					while (pmultivalues.getProperty(String.valueOf(i)) != null) {
						ret.add(pmultivalues.getProperty(String.valueOf(i++)));
					}
					return ret;
				} else {
					Vector<String> ret = new Vector<String>(1);
					ret.add(p.getProperty(key));
					return ret;
				}
			}
		} finally {
			if (STATIC.NAMESPACE_CORE.equals(namespace) && STATIC.REMOTE_CONFIGFILE_PENDING.equals(configfile)
					&& STATIC.REMOTE_CONFIGVAL_PENDING.equals(p.getProperty(key))) {
				synchronized (Fileutil.syncfile(target)) {
					try {
						os = Files.newOutputStream(target);
						p.put(key, "");
						p.store(os, "Properties of " + configfile);
					} catch (Exception e) {
						// do nothing
					} finally {
						if (os != null) {
							try {
								os.close();
							} catch (Exception e) {
								// do nothing
							}
						}
					}
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// do nothing
				}
			}

			if (ismultivalues != null) {
				try {
					ismultivalues.close();
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}

	public static Vector<String> readallmultivaluekeys(String namespace, String configfile) throws Exception {
		Properties p = new Properties();
		
		Path target = file(namespace, configfile, false);
		if (!Files.exists(target.getParent())) {
			return null;
		}
		InputStream is = null;

		try {
			if (Files.exists(target)) {
				is = Files.newInputStream(target);
				p.load(is);
				Vector<String> returnvalue = new Vector<String>();
				for (Object key:p.keySet()) {
					if (p.getProperty(key.toString()).equalsIgnoreCase(multivalues)) {
						returnvalue.add(key.toString());
					}
				}
				return returnvalue;
			} else {
				return null;
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}
	
	public static String readone(String key, String namespace, String configfile) throws Exception {
		Vector<String> returnvalue = read(key, namespace, configfile);
		if (returnvalue != null && !returnvalue.isEmpty()) {
			return returnvalue.get(0);
		}
		return null;
	}

	public static Vector<String> readmany(String key, String namespace, String configfile) throws Exception {
		return read(key, namespace, configfile);
	}

	public static Path file(String namespace, String configfile, boolean createfolder) throws Exception {
		Path folder = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace);
		if (createfolder && !Files.exists(folder)) {
			Files.createDirectories(folder);
		}
		return folder.resolve(configfile);
	}

}
