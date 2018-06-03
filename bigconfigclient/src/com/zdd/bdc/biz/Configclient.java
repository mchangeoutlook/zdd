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
	
	private static Map<String, Map<String, Map<String, String>>> confignsfileskeysvaluescache = new Hashtable<String, Map<String, Map<String, String>>>();
	private static Path configfolder = Paths.get("config");

	static {
		if (Files.exists(configfolder) && Files.isDirectory(configfolder)) {
			try {
				Files.walk(configfolder).filter(Files::isRegularFile).forEach(pathfile -> {
					
					final StringBuffer ns = new StringBuffer();
					try {
						ns.append(URLDecoder.decode(pathfile.getParent().getFileName().toString(),"UTF-8"));
					} catch (Exception e) {
						System.out.println(new Date() + " ==== System exited due to below exception:");
						e.printStackTrace();
						System.exit(1);
					}
					String namespace = ns.toString();
					final StringBuffer f = new StringBuffer();
					try {
						f.append(URLDecoder.decode(pathfile.getFileName().toString(),"UTF-8"));
					} catch (Exception e) {
						System.out.println(new Date() + " ==== System exited due to below exception:");
						e.printStackTrace();
						System.exit(1);
					}
					String file = f.toString();
					if (confignsfileskeysvaluescache.get(namespace) == null) {
						confignsfileskeysvaluescache.put(namespace, new Hashtable<String, Map<String, String>>());
					}
					try {
						if (confignsfileskeysvaluescache.get(namespace).get(file) == null) {
							confignsfileskeysvaluescache.get(namespace).put(file,
									new Hashtable<String, String>());
						}
						
						Files.lines(pathfile, Charset.forName("UTF-8")).forEach(line -> {
							if (line.indexOf("#") > 0) {
								String encodedkey = line.substring(0, line.indexOf("#"));
								String encodedvalue = "";
								if (line.length() > line.indexOf("#") + 1) {
									encodedvalue = line.substring(line.indexOf("#") + 1);
								}
								try {
									confignsfileskeysvaluescache.get(namespace).get(file).put(
											URLDecoder.decode(encodedkey, "UTF-8"),
											URLDecoder.decode(encodedvalue, "UTF-8"));
								} catch (Exception e) {
									System.out.println(new Date() + " ==== System exited due to below exception:");
									e.printStackTrace();
									System.exit(1);
								}
							}
						});
					} catch (Exception e) {
						System.out.println(new Date() + " ==== System exited due to below exception:");
						e.printStackTrace();
						System.exit(1);
					}
				});
			} catch (Exception e) {
				System.out.println(new Date() + " ==== System exited due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				int interval=0;
				try {
					interval = Integer.parseInt(Configclient.getinstance("core", "core").read("updateconfigcache.intervalseconds"));
				} catch (Exception e) {
					System.out.println(new Date() + " ==== System exited due to below exception:");
					e.printStackTrace();
					System.exit(1);
				}
				if (interval>0) {
					System.out.println(new Date()+" ==== Started auto update config every "+interval+" seconds");
					while(true) {
						try {
							Thread.sleep(interval*1000);
						} catch (InterruptedException e) {
							//do nothing
						}
						Map<String, Object> params = new Hashtable<String, Object>(2);
						params.put("data", confignsfileskeysvaluescache);
						params.put("action", "read");
						Map<String, Map<String, Map<String, String>>> res = null;
						try {
							res = (Map<String, Map<String, Map<String, String>>>) Objectutil
									.convert(Theclient.request(confignsfileskeysvaluescache.get("core").get("core").get("configserverip"), 
											Integer.parseInt(confignsfileskeysvaluescache.get("core").get("core").get("configserverport")), 
											Objectutil.convert(params), null));
							for (String namespace:res.keySet()) {
								for (String file:res.get(namespace).keySet()) {
									boolean changed = false;
									for (String configkey:res.get(namespace).get(file).keySet()) {
										if (!res.get(namespace).get(file).get(configkey).equals(confignsfileskeysvaluescache.get(namespace).get(file).get(configkey))) {
											confignsfileskeysvaluescache.get(namespace).get(file).put(configkey,res.get(namespace).get(file).get(configkey));
											changed = true;
										}
									}
									if (changed) {
										Path target = target(namespace, file);
										if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
											Files.createDirectories(target.getParent());
										}
										Files.write(target, ("#Auto generated on " + new Date() + System.lineSeparator()).getBytes("UTF-8"),
												StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
	
										for (String configkey : confignsfileskeysvaluescache.get(namespace).get(file).keySet()) {
											Files.write(target, (URLEncoder.encode(configkey, "UTF-8") + "#"
													+ URLEncoder.encode(confignsfileskeysvaluescache.get(namespace).get(file).get(configkey),
															"UTF-8")
													+ System.lineSeparator()).getBytes("UTF-8"), StandardOpenOption.CREATE,
													StandardOpenOption.APPEND, StandardOpenOption.SYNC);
										}
									}
								}

							}
						} catch (Exception e) {
							//do nothing
							e.printStackTrace();
						}
					}
				}
			}
			
		}).start();
	}

	private String ns = null;
	private String file = null;
	private Map<String, Map<String, Map<String, String>>> configkeysvalues = null;

	private Configclient(String namespace, String configfile) {
		ns = namespace;
		file = configfile;
	}

	public static Configclient getinstance(String namespace, String configfile) {
		return new Configclient(namespace, configfile);
	}

	public String read(String configkey) {
		try {
			String configvalue = confignsfileskeysvaluescache.get(ns).get(file).get(configkey);
			if (configvalue == null) {
				throw new Exception("notexit");
			}
			return configvalue;
		}catch(Exception e) {
			return readcache(ns, file, configkey);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static synchronized String readcache(String namespace, String file, String configkey) {
		if (confignsfileskeysvaluescache.get(namespace)==null) {
			confignsfileskeysvaluescache.put(namespace, new Hashtable<String, Map<String, String>>(1));
		}
		if (confignsfileskeysvaluescache.get(namespace).get(file)==null) {
			confignsfileskeysvaluescache.get(namespace).put(file, new Hashtable<String, String>(1));
		}
		if (confignsfileskeysvaluescache.get(namespace).get(file).get(configkey)==null) {
			Map<String, Map<String, Map<String, String>>> configkeysvalues = new Hashtable<String, Map<String, Map<String, String>>>(1);
			configkeysvalues.put(namespace, new Hashtable<String, Map<String, String>>(1));
			configkeysvalues.get(namespace).put(file, new Hashtable<String, String>(1));
			configkeysvalues.get(namespace).get(file).put(configkey, "");
			Map<String, Object> params = new Hashtable<String, Object>(2);
			params.put("data", configkeysvalues);
			params.put("action", "read");
			Map<String, Map<String, Map<String, String>>> res = null;
			try {
				res = (Map<String, Map<String, Map<String, String>>>) Objectutil
						.convert(Theclient.request(confignsfileskeysvaluescache.get("core").get("core").get("configserverip"), 
								Integer.parseInt(confignsfileskeysvaluescache.get("core").get("core").get("configserverport")), 
								Objectutil.convert(params), null));
				confignsfileskeysvaluescache.get(namespace).get(file).put(configkey,res.get(namespace).get(file).get(configkey));
			} catch (Exception e) {
				return "";
			}
		} 
		return confignsfileskeysvaluescache.get(namespace).get(file).get(configkey);
	}

	public Configclient configkeys(int numofkeys) {
		configkeysvalues = new Hashtable<String, Map<String, Map<String, String>>>(1);
		configkeysvalues.put(ns, new Hashtable<String, Map<String, String>>(1));
		configkeysvalues.get(ns).put(file, new Hashtable<String, String>(numofkeys));
		return this;
	}

	public Configclient add4create(String configkey, String configvalue) {
		configkeysvalues.get(ns).get(file).put(configkey, configvalue);
		return this;
	}

	public void create() throws Exception {
		if (configkeysvalues.isEmpty()) {
			throw new Exception(".configkeys.add4create.create");
		}
		try {
			Map<String, Object> params = new Hashtable<String, Object>(5);
			params.put("data", configkeysvalues);
			params.put("action", "change");
			Objectutil.convert(Theclient.request(confignsfileskeysvaluescache.get("core").get("core").get("configserverip"), 
					Integer.parseInt(confignsfileskeysvaluescache.get("core").get("core").get("configserverport")), Objectutil.convert(params), null));
		} finally {
			clear();
		}
	}

	private void clear() {
		ns = null;
		if (configkeysvalues != null) {
			configkeysvalues.clear();
		}
		configkeysvalues = null;
	}
	
	private static Path target(String namespace, String file) throws Exception {
		if (namespace.isEmpty()) {
			throw new Exception("emptyns");
		}
		if (file.isEmpty()) {
			throw new Exception("emptyfile");
		}
		return configfolder.resolve(URLEncoder.encode(namespace, "UTF-8") + "/" + URLEncoder.encode(file, "UTF-8"));
	}

}
