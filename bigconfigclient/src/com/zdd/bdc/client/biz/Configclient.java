package com.zdd.bdc.client.biz;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;

public class Configclient {

	public static String ip = null;
	public static int port = -1;
	
	public static boolean running = true;
	private static Map<String, Map<String, Map<String, String>>> nsfilekeyvalue = new Hashtable<String, Map<String, Map<String, String>>>();
	private static Map<String, String> nsfilechanged = new Hashtable<String, String>();
	static {
		if (Files.exists(CS.LOCAL_CONFIGFOLDER) && Files.isDirectory(CS.LOCAL_CONFIGFOLDER)) {
			try {
				ip = CS.localip();
				Files.walk(CS.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = pathfile.getParent().getFileName().toString();
							String file = pathfile.getFileName().toString();
							if (nsfilekeyvalue.get(namespace) == null) {
								nsfilekeyvalue.put(namespace, new Hashtable<String, Map<String, String>>());
							}
							if (nsfilekeyvalue.get(namespace).get(file) == null) {
								nsfilekeyvalue.get(namespace).put(file, new Hashtable<String, String>());
							}
							List<String> lines = Files.readAllLines(pathfile, Charset.forName("UTF-8"));
							for (String line:lines) {
								String[] keyval = CS.splitenc(line);
								if (keyval.length > 0 && !keyval[0].isEmpty()) {
									try {
										if (keyval.length==1) {
											nsfilekeyvalue.get(namespace).get(file).put(
													keyval[0],
													"");
										} else {
											nsfilekeyvalue.get(namespace).get(file).put(
													keyval[0],
													keyval[1]);
										}
									} catch (Exception e) {
										System.out.println(new Date() + " ==== System exited when handling line ["
												+ line + "] of file [" + pathfile.toAbsolutePath()
												+ "] due to below exception:");
										e.printStackTrace();
										System.exit(1);
									}
								}
							};
						} catch (Exception e) {
							System.out.println(new Date() + " ==== System exited when reading [" + pathfile
									+ "] due to below exception:");
							e.printStackTrace();
							System.exit(1);
						}
					}
				});
			} catch (Exception e) {
				System.out.println(new Date() + " ==== System exited when reading folder ["
						+ CS.LOCAL_CONFIGFOLDER.toAbsolutePath() + "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.out.println(new Date() + " ==== loaded local config cache ["+nsfilekeyvalue+"] under [" + nsfilekeyvalue.size() + "] namespaces");

		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				System.out.println(new Date() + " ==== Started auto update config every "
						+ Integer.parseInt(
								Configclient.getinstance(CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE).read(CS.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS))
						+ " seconds");
				while (running) {
					try {
						Thread.sleep(Integer.parseInt(
								Configclient.getinstance(CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE).read(CS.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS))
								* 1000);
					} catch (InterruptedException e) {
						// do nothing
					}
					try {
						Map<String, Map<String, Map<String, String>>> temp = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Objectutil.convert(nsfilekeyvalue));
						Map<String, Object> params = new Hashtable<String, Object>(2);
						params.put(CS.PARAM_DATA_KEY, temp);
						params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
						Map<String, Map<String, Map<String, String>>> res = null;
						res = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Theclient.request(nsfilekeyvalue.get(CS.NAMESPACE_CORE).get(CS.REMOTE_CONFIG_CORE).get(CS.REMOTE_CONFIGKEY_CONFIGSERVERIP),
										Integer.parseInt(
												nsfilekeyvalue.get(CS.NAMESPACE_CORE).get(CS.REMOTE_CONFIG_CORE).get(CS.REMOTE_CONFIGKEY_CONFIGSERVERPORT)),
										Objectutil.convert(params), null, null));
						for (String namespace : temp.keySet()) {
							if (res.get(namespace) == null) {
								nsfilekeyvalue.remove(namespace);
								Path folder = targetconfigfile(namespace, null);
								if (Files.exists(folder)) {
									String[] subfiles = folder.toFile().list();
									for (String file : subfiles) {
										Files.deleteIfExists(folder.resolve(file));
									}
									Files.deleteIfExists(folder);
								}
							} else {
								for (String file : temp.get(namespace).keySet()) {
									if (res.get(namespace).get(file) == null) {
										nsfilekeyvalue.get(namespace).remove(file);
										Files.deleteIfExists(targetconfigfile(namespace, file));
									} else {
										boolean changed = false;
										for (String key : temp.get(namespace).get(file).keySet()) {
											if (res.get(namespace).get(file).get(key) == null) {
												nsfilekeyvalue.get(namespace).get(file).remove(key);
												changed = true;
											} else if (!res.get(namespace).get(file).get(key)
													.equals(nsfilekeyvalue.get(namespace).get(file).get(key))) {
												nsfilekeyvalue.get(namespace).get(file).put(key,
														res.get(namespace).get(file).get(key));
												changed = true;
											}
										}
										Path target = targetconfigfile(namespace, file);
										if (!Files.exists(target) || changed
												|| nsfilechanged.get(namespace + file) != null) {
											if (!Files.exists(target) && target.getParent() != null
													&& !Files.exists(target.getParent())) {
												Files.createDirectories(target.getParent());
											}
											Files.write(target,
													CS.tobytes(CS.splitenc("","Auto generated on " + new Date()) + System.lineSeparator()),
													StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
													StandardOpenOption.SYNC);
											Object[] configkeys = nsfilekeyvalue.get(namespace).get(file).keySet()
													.toArray();
											for (Object configkey : configkeys) {
												Files.write(target,
														CS.tobytes(CS.splitenc(configkey.toString(), nsfilekeyvalue.get(namespace)
																.get(file).get(configkey.toString())) + System.lineSeparator()),
														StandardOpenOption.CREATE, StandardOpenOption.APPEND,
														StandardOpenOption.SYNC);
											}
											nsfilechanged.remove(namespace + file);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						// do nothing
					}
				}
			}

		}).start();
	}

	private String ns = null;
	private String file = null;

	private Configclient(String namespace, String configfile) {
		ns = namespace;
		file = configfile;
	}

	public static Configclient getinstance(String namespace, String configfile) {
		return new Configclient(namespace, configfile);
	}

	public String read(String configkey) {
		try {
			String returnvalue = nsfilekeyvalue.get(ns).get(file).get(configkey);
			if (returnvalue == null) {
				throw new Exception("needrefreshcache");
			}
			return returnvalue;
		} catch (Exception e) {
			return refreshcache(ns, file, configkey);
		}
	}

	@SuppressWarnings("unchecked")
	private static synchronized String refreshcache(String namespace, String file, String configkey) {
		Map<String, Map<String, Map<String, String>>> config = new Hashtable<String, Map<String, Map<String, String>>>(
				1);
		config.put(namespace, new Hashtable<String, Map<String, String>>(1));
		config.get(namespace).put(file, new Hashtable<String, String>(1));
		config.get(namespace).get(file).put(configkey, "");
		Map<String, Object> params = new Hashtable<String, Object>(2);
		params.put(CS.PARAM_DATA_KEY, config);
		params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
		params.put(CS.PARAM_ADDITIONAL, CS.splitiport(ip, String.valueOf(port)));
		Map<String, Map<String, Map<String, String>>> res = null;
		try {
			res = (Map<String, Map<String, Map<String, String>>>) Objectutil
					.convert(Theclient.request(nsfilekeyvalue.get(CS.NAMESPACE_CORE).get(CS.REMOTE_CONFIG_CORE).get(CS.REMOTE_CONFIGKEY_CONFIGSERVERIP),
							Integer.parseInt(nsfilekeyvalue.get(CS.NAMESPACE_CORE).get(CS.REMOTE_CONFIG_CORE).get(CS.REMOTE_CONFIGKEY_CONFIGSERVERPORT)),
							Objectutil.convert(params), null, null));
			if (res.get(namespace) == null || res.get(namespace).get(file) == null
					|| res.get(namespace).get(file).get(configkey) == null) {
				return null;
			}
			if (nsfilekeyvalue.get(namespace) == null) {
				nsfilekeyvalue.put(namespace, new Hashtable<String, Map<String, String>>(1));
			}
			if (nsfilekeyvalue.get(namespace).get(file) == null) {
				nsfilekeyvalue.get(namespace).put(file, new Hashtable<String, String>(1));
			}
			nsfilekeyvalue.get(namespace).get(file).put(configkey, res.get(namespace).get(file).get(configkey));
			nsfilechanged.put(namespace + file, "changed");
		} catch (Exception e) {
			return null;
		}
		return nsfilekeyvalue.get(namespace).get(file).get(configkey);
	}

	private static Path targetconfigfile(String namespace, String file) throws Exception {
		Path folder = CS.LOCAL_CONFIGFOLDER.resolve(namespace);
		if (file == null) {
			return folder;
		} else {
			return folder.resolve(file);
		}
	}

}
