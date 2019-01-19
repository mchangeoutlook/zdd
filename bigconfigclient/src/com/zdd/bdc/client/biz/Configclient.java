package com.zdd.bdc.client.biz;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;

public class Configclient {

	public static String ip = STATIC.localip();
	public static int port = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
	
	public static boolean running = true;
	private static Map<String, Map<String, Map<String, String>>> nsfilekeyvalue = new Hashtable<String, Map<String, Map<String, String>>>();
	private static Map<String, String> nsfilechanged = new Hashtable<String, String>();
	static {
		System.out.println(new Date()+" ==== initing config client on ip=["+ip+"] with process id ["+port+"], which is also the default port and it may be changed later.");
		
		if (Files.exists(STATIC.LOCAL_CONFIGFOLDER) && Files.isDirectory(STATIC.LOCAL_CONFIGFOLDER)) {
			try {
				Files.walk(STATIC.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
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
								String[] keyval = STATIC.splitenc(line);
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
						+ STATIC.LOCAL_CONFIGFOLDER.toAbsolutePath() + "] due to below exception:");
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
								Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE).read(STATIC.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS))
						+ " seconds");
				while (running) {
					try {
						Thread.sleep(Integer.parseInt(
								Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE).read(STATIC.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS))
								* 1000);
					} catch (InterruptedException e) {
						// do nothing
					}
					try {
						Map<String, Map<String, Map<String, String>>> temp = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Objectutil.convert(nsfilekeyvalue));
						Map<String, Object> params = new Hashtable<String, Object>(2);
						params.put(STATIC.PARAM_DATA_KEY, temp);
						params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
						Map<String, Map<String, Map<String, String>>> res = null;
						res = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Theclient.request(nsfilekeyvalue.get(STATIC.NAMESPACE_CORE).get(STATIC.REMOTE_CONFIG_CORE).get(STATIC.REMOTE_CONFIGKEY_CONFIGSERVERIP),
										Integer.parseInt(
												nsfilekeyvalue.get(STATIC.NAMESPACE_CORE).get(STATIC.REMOTE_CONFIG_CORE).get(STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT)),
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
											if (STATIC.NAMESPACE_CORE.equals(namespace)&&STATIC.REMOTE_CONFIG_PENDING.equals(file)) {
												//do nothing
											} else {
												if (!Files.exists(target) && target.getParent() != null
														&& !Files.exists(target.getParent())) {
													Files.createDirectories(target.getParent());
												}
												Files.write(target,
														STATIC.tobytes(STATIC.splitenc("","Auto generated on " + new Date()) + System.lineSeparator()),
														StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
														StandardOpenOption.SYNC);
												Object[] configkeys = nsfilekeyvalue.get(namespace).get(file).keySet()
														.toArray();
												for (Object configkey : configkeys) {
													Files.write(target,
															STATIC.tobytes(STATIC.splitenc(configkey.toString(), nsfilekeyvalue.get(namespace)
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
			if ("needrefreshcache".equals(e.getMessage())) {
				return refreshcache(ns, file, configkey);
			} else {
				return null;
			}
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
		params.put(STATIC.PARAM_DATA_KEY, config);
		params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
		params.put(STATIC.PARAM_ADDITIONAL, STATIC.splitiport(ip, String.valueOf(port)));
		Map<String, Map<String, Map<String, String>>> res = null;
		try {
			res = (Map<String, Map<String, Map<String, String>>>) Objectutil
					.convert(Theclient.request(nsfilekeyvalue.get(STATIC.NAMESPACE_CORE).get(STATIC.REMOTE_CONFIG_CORE).get(STATIC.REMOTE_CONFIGKEY_CONFIGSERVERIP),
							Integer.parseInt(nsfilekeyvalue.get(STATIC.NAMESPACE_CORE).get(STATIC.REMOTE_CONFIG_CORE).get(STATIC.REMOTE_CONFIGKEY_CONFIGSERVERPORT)),
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
		Path folder = STATIC.LOCAL_CONFIGFOLDER.resolve(namespace);
		if (file == null) {
			return folder;
		} else {
			return folder.resolve(file);
		}
	}

}
