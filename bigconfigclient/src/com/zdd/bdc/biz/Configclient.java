package com.zdd.bdc.biz;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.util.Objectutil;

public class Configclient {

	private static Map<String, Map<String, Map<String, String>>> nsfilekeyvalue = new Hashtable<String, Map<String, Map<String, String>>>();
	private static Path configfolder = Paths.get("config");
	private static Map<String, String> nsfilechanged = new Hashtable<String, String>();
	static {
		if (Files.exists(configfolder) && Files.isDirectory(configfolder)) {
			try {
				Files.walk(configfolder).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = URLDecoder.decode(pathfile.getParent().getFileName().toString(),
									"UTF-8");
							String file = URLDecoder.decode(pathfile.getFileName().toString(), "UTF-8");
							if (nsfilekeyvalue.get(namespace) == null) {
								nsfilekeyvalue.put(namespace, new Hashtable<String, Map<String, String>>());
							}
							if (nsfilekeyvalue.get(namespace).get(file) == null) {
								nsfilekeyvalue.get(namespace).put(file, new Hashtable<String, String>());
							}

							Files.lines(pathfile, Charset.forName("UTF-8")).forEach(line -> {
								if (line.indexOf("#") > 0) {
									String encodedkey = line.substring(0, line.indexOf("#"));
									String encodedvalue = "";
									if (line.length() > line.indexOf("#") + 1) {
										encodedvalue = line.substring(line.indexOf("#") + 1);
									}
									try {
										nsfilekeyvalue.get(namespace).get(file).put(
												URLDecoder.decode(encodedkey, "UTF-8"),
												URLDecoder.decode(encodedvalue, "UTF-8"));
									} catch (Exception e) {
										System.out.println(new Date() + " ==== System exited when handling line ["
												+ line + "] of file [" + pathfile.toAbsolutePath()
												+ "] due to below exception:");
										e.printStackTrace();
										System.exit(1);
									}
								}
							});
						} catch (Exception e) {
							System.out.println(new Date() + " ==== System exited when reading [" + pathfile
									+ "]due to below exception:");
							e.printStackTrace();
							System.exit(1);
						}
					}
				});
			} catch (Exception e) {
				System.out.println(new Date() + " ==== System exited when reading folder ["
						+ configfolder.toAbsolutePath() + "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.out.println(new Date() + " ==== loaded local config cache [" + nsfilekeyvalue + "]");

		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				System.out.println(new Date() + " ==== Started auto update config every "
						+ Integer.parseInt(
								Configclient.getinstance("core", "core").read("updateconfigcache.intervalseconds"))
						+ " seconds");
				while (true) {
					try {
						Thread.sleep(Integer.parseInt(
								Configclient.getinstance("core", "core").read("updateconfigcache.intervalseconds"))
								* 1000);
					} catch (InterruptedException e) {
						// do nothing
					}
					try {
						Map<String, Map<String, Map<String, String>>> temp = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Objectutil.convert(nsfilekeyvalue));
						Map<String, Object> params = new Hashtable<String, Object>(2);
						params.put("data", temp);
						params.put("action", "read");
						Map<String, Map<String, Map<String, String>>> res = null;
						res = (Map<String, Map<String, Map<String, String>>>) Objectutil
								.convert(Theclient.request(nsfilekeyvalue.get("core").get("core").get("configserverip"),
										Integer.parseInt(
												nsfilekeyvalue.get("core").get("core").get("configserverport")),
										Objectutil.convert(params), null, null));
						for (String namespace : temp.keySet()) {
							if (res.get(namespace) == null) {
								nsfilekeyvalue.remove(namespace);
								Path folder = target(namespace, null);
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
										Files.deleteIfExists(target(namespace, file));
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
										Path target = target(namespace, file);
										if (!Files.exists(target) || changed
												|| nsfilechanged.get(namespace + file) != null) {
											if (!Files.exists(target) && target.getParent() != null
													&& !Files.exists(target.getParent())) {
												Files.createDirectories(target.getParent());
											}
											Files.write(target,
													("#Auto generated on " + new Date() + System.lineSeparator()
															+ "#line format: URLEncoded key#URLEncoded value"
															+ System.lineSeparator()).getBytes("UTF-8"),
													StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
													StandardOpenOption.SYNC);
											Object[] configkeys = nsfilekeyvalue.get(namespace).get(file).keySet()
													.toArray();
											for (Object configkey : configkeys) {
												Files.write(target,
														(URLEncoder.encode(configkey.toString(), "UTF-8") + "#"
																+ URLEncoder.encode(nsfilekeyvalue.get(namespace)
																		.get(file).get(configkey.toString()), "UTF-8")
																+ System.lineSeparator()).getBytes("UTF-8"),
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
		params.put("data", config);
		params.put("action", "read");
		Map<String, Map<String, Map<String, String>>> res = null;
		try {
			res = (Map<String, Map<String, Map<String, String>>>) Objectutil
					.convert(Theclient.request(nsfilekeyvalue.get("core").get("core").get("configserverip"),
							Integer.parseInt(nsfilekeyvalue.get("core").get("core").get("configserverport")),
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

	private static Path target(String namespace, String file) throws Exception {
		Path folder = configfolder.resolve(URLEncoder.encode(namespace, "UTF-8"));
		if (file == null) {
			return folder;
		} else {
			return folder.resolve(URLEncoder.encode(file, "UTF-8"));
		}
	}

}
