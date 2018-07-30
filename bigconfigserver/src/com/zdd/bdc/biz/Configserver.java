package com.zdd.bdc.biz;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Objectutil;

public class Configserver implements Theserverprocess {

	private static Map<String, Map<String, Map<String, String>>> confignsfileskeysvalues = new Hashtable<String, Map<String, Map<String, String>>>();
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
					if (confignsfileskeysvalues.get(namespace) == null) {
						confignsfileskeysvalues.put(namespace, new Hashtable<String, Map<String, String>>());
					}
					try {
						final StringBuffer f = new StringBuffer();
						try {
							f.append(URLDecoder.decode(pathfile.getFileName().toString(),"UTF-8"));
						} catch (Exception e) {
							System.out.println(new Date() + " ==== System exited due to below exception:");
							e.printStackTrace();
							System.exit(1);
						}
						String file = f.toString();
						if (confignsfileskeysvalues.get(namespace).get(file) == null) {
							confignsfileskeysvalues.get(namespace).put(file,
									new Hashtable<String, String>());
						}
						if (confignsfileskeysvalues.get(namespace).get("bigindex") == null) {
							confignsfileskeysvalues.get(namespace).put("bigindex", new Hashtable<String, String>());
						}
						if (confignsfileskeysvalues.get(namespace).get("bigdata") == null) {
							confignsfileskeysvalues.get(namespace).put("bigdata", new Hashtable<String, String>());
						}
						if (confignsfileskeysvalues.get(namespace).get("bigindexgen") == null) {
							confignsfileskeysvalues.get(namespace).put("bigindexgen", new Hashtable<String, String>());
						}
						if (confignsfileskeysvalues.get(namespace).get("bigdatagen") == null) {
							confignsfileskeysvalues.get(namespace).put("bigdatagen", new Hashtable<String, String>());
						}

						Files.lines(pathfile, Charset.forName("UTF-8")).forEach(line -> {
							if (line.indexOf("#") > 0) {
								String encodedkey = line.substring(0, line.indexOf("#"));
								String encodedvalue = "";
								if (line.length() > line.indexOf("#") + 1) {
									encodedvalue = line.substring(line.indexOf("#") + 1);
								}
								try {
									if ("bigindex".equals(file)) {
										Configserver.populatebigindexgen(namespace,
												URLDecoder.decode(encodedkey, "UTF-8"),
												URLDecoder.decode(encodedvalue, "UTF-8"));
									}
									confignsfileskeysvalues.get(namespace).get(file).put(
											URLDecoder.decode(encodedkey, "UTF-8"),
											URLDecoder.decode(encodedvalue, "UTF-8"));
									System.out.println("loaded ["+namespace+"]["+file+"]["+URLDecoder.decode(encodedkey, "UTF-8")+"]["+confignsfileskeysvalues.get(namespace).get(file).get(
											URLDecoder.decode(encodedkey, "UTF-8"))+"]");
								} catch (Exception e) {
									System.out.println(new Date() + " ==== System exited due to below exception:");
									e.printStackTrace();
									System.exit(1);
								}
							}
						});
						if (!confignsfileskeysvalues.get(namespace).get("bigdata").isEmpty()) {
							Configserver.populatebigdatagen(namespace,
									confignsfileskeysvalues.get(namespace).get("bigdata").keySet());
						}
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
	}

	public static String read(String namespace, String file, String configkey) {
		if (Files.exists(configfolder.resolve(namespace).resolve(file))){
			try {
				List<String> lines = Files.readAllLines(configfolder.resolve(namespace).resolve(file),Charset.forName("UTF-8"));
				for (String line:lines) {
					if (line.indexOf("#") > 0) {
						String encodedkey = line.substring(0, line.indexOf("#"));
						String encodedvalue = "";
						if (line.length() > line.indexOf("#") + 1) {
							encodedvalue = line.substring(line.indexOf("#") + 1);
						}
						if (URLDecoder.decode(encodedkey,"UTF-8").equals(configkey)) {
							return URLDecoder.decode(encodedvalue,"UTF-8");
						}
					}
				}
			} catch (Exception e) {
				// do nothing
			}
		}
		return null;
	}

	private static void populatebigindexgen(String namespace, String configkey, String configvalue) throws Exception {
		if (configkey.contains("-")) {
			int start = Integer.parseInt(configkey.split("-")[0]);
			int end = Integer.parseInt(configkey.split("-")[1]);
			if (start < 0 || start > end) {
				throw new Exception("wrongconfigkey-" + configkey);
			}
			for (int i = start; i <= end; i++) {
				confignsfileskeysvalues.get(namespace).get("bigindexgen").put(String.valueOf(i), configvalue);
			}
		} else {
			confignsfileskeysvalues.get(namespace).get("bigindexgen").put(configkey, configvalue);
		}
	}

	private static void populatebigdatagen(String namespace, Set<String> configkeys) throws Exception {
		Object[] sortedconfigkeys = configkeys.toArray();
		Arrays.sort(sortedconfigkeys);
		for (int i = 0; i < sortedconfigkeys.length; i++) {
			confignsfileskeysvalues.get(namespace).get("bigdatagen").put(sortedconfigkeys[i].toString(),
					confignsfileskeysvalues.get(namespace).get("bigdata").get(sortedconfigkeys[i].toString()));
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(sortedconfigkeys[i].toString()));
			c.add(Calendar.DATE, 1);
			while (i + 1 < sortedconfigkeys.length && !new SimpleDateFormat("yyyy-MM-dd").format(c.getTime())
					.equals(sortedconfigkeys[i + 1].toString())) {
				confignsfileskeysvalues.get(namespace).get("bigdatagen").put(
						new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()),
						confignsfileskeysvalues.get(namespace).get("bigdata").get(sortedconfigkeys[i].toString()));
				c.add(Calendar.DATE, 1);
			}
			if (i + 1 >= sortedconfigkeys.length) {
				while (c.getTime().compareTo(new Date()) <= 0) {
					confignsfileskeysvalues.get(namespace).get("bigdatagen").put(
							new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()),
							confignsfileskeysvalues.get(namespace).get("bigdata").get(sortedconfigkeys[i].toString()));
					c.add(Calendar.DATE, 1);
				}
			}
		}
	}

	private Map<String, Map<String, Map<String, String>>> returnvalue = null;

	@SuppressWarnings("unchecked")
	@Override
	public void start(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		returnvalue = (Map<String, Map<String, Map<String, String>>>) params.get("data");
		if ("read".equals(params.get("action").toString())) {
			Object[] ns = returnvalue.keySet().toArray();
			for (Object nsobj : ns) {
				String namespace = nsobj.toString();
				if (returnvalue.get(namespace) != null) {
					Object[] files = returnvalue.get(namespace).keySet().toArray();
					for (Object fileobj : files) {
						String file = fileobj.toString();
						if (returnvalue.get(namespace)!=null&&returnvalue.get(namespace).get(file) != null) {
							Object[] configkeys = returnvalue.get(namespace).get(file).keySet().toArray();

							for (Object configkeyobj : configkeys) {
								String configkey = configkeyobj.toString();
								if (confignsfileskeysvalues.get(namespace) != null) {
									if (confignsfileskeysvalues.get(namespace).get(file) != null) {
										if (file.equals("bigdata")) {
											file = "bigdatagen";
										} else if (file.equals("bigindex")) {
											file = "bigindexgen";
										}
										if (confignsfileskeysvalues.get(namespace).get(file).get(configkey) != null) {
											if (file.equals("bigdatagen")) {
												returnvalue.get(namespace).get("bigdata").put(configkey,
														confignsfileskeysvalues.get(namespace).get(file)
																.get(configkey));
											} else if (file.equals("bigindexgen")) {
												returnvalue.get(namespace).get("bigindex").put(configkey,
														confignsfileskeysvalues.get(namespace).get(file)
																.get(configkey));
											} else {
												returnvalue.get(namespace).get(file).put(configkey,
														confignsfileskeysvalues.get(namespace).get(file)
																.get(configkey));
											}
										} else {
											if (file.equals("bigdatagen")) {
												Object[] temp = confignsfileskeysvalues.get(namespace).get("bigdata")
														.keySet().toArray();
												Arrays.sort(temp);
												String value = confignsfileskeysvalues.get(namespace).get("bigdata")
														.get(temp[temp.length - 1].toString());
												returnvalue.get(namespace).get("bigdata").put(configkey, value);
												confignsfileskeysvalues.get(namespace).get("bigdatagen").put(configkey,
														value);
											} else if (file.equals("bigindexgen")) {
												returnvalue.get(namespace).get("bigindex").remove(configkey);
											} else {
												returnvalue.get(namespace).get(file).remove(configkey);
											}
										}
									} else {
										returnvalue.get(namespace).remove(file);
									}
								} else {
									returnvalue.remove(namespace);
								}
							}
						}
					}
				}
			}
		} else if ("change".equals(params.get("action").toString())) {
			change(returnvalue);
		} else {
			throw new Exception("notsupport-" + params.get("action"));
		}
	}

	public static synchronized void change(Map<String, Map<String, Map<String, String>>> tochange) throws Exception {
		for (String namespace : tochange.keySet()) {
			for (String file : tochange.get(namespace).keySet()) {
				boolean changed = false;
				for (String configkey : tochange.get(namespace).get(file).keySet()) {
					if (confignsfileskeysvalues.get(namespace) == null) {
						confignsfileskeysvalues.put(namespace, new Hashtable<String, Map<String, String>>());
					}
					if (confignsfileskeysvalues.get(namespace).get(file) == null) {
						confignsfileskeysvalues.get(namespace).put(file, new Hashtable<String, String>());
					}
					if (confignsfileskeysvalues.get(namespace).get("bigindexgen") == null) {
						confignsfileskeysvalues.get(namespace).put("bigindexgen", new Hashtable<String, String>());
					}
					if (confignsfileskeysvalues.get(namespace).get("bigdatagen") == null) {
						confignsfileskeysvalues.get(namespace).put("bigdatagen", new Hashtable<String, String>());
					}

					if (tochange.get(namespace).get(file).get(configkey) != null) {
						confignsfileskeysvalues.get(namespace).get(file).put(configkey,
								tochange.get(namespace).get(file).get(configkey));
						if (file.equals("bigindex")) {
							Configserver.populatebigindexgen(namespace, configkey,
									tochange.get(namespace).get(file).get(configkey));
						}
						changed = true;
					}
				}

				if (changed) {
					if (file.equals("bigdata")) {
						Configserver.populatebigdatagen(namespace,
								confignsfileskeysvalues.get(namespace).get(file).keySet());
					}
					Path target = target(namespace, file);
					if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
						Files.createDirectories(target.getParent());
					}
					Files.write(target, ("#Auto generated on " + new Date() + System.lineSeparator()).getBytes("UTF-8"),
							StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);

					for (String configkey : confignsfileskeysvalues.get(namespace).get(file).keySet()) {
						Files.write(target, (URLEncoder.encode(configkey, "UTF-8") + "#"
								+ URLEncoder.encode(confignsfileskeysvalues.get(namespace).get(file).get(configkey),
										"UTF-8")
								+ System.lineSeparator()).getBytes("UTF-8"), StandardOpenOption.CREATE,
								StandardOpenOption.APPEND, StandardOpenOption.SYNC);
					}
				}
			}

		}

	}

	@Override
	public void process(byte[] b) throws Exception {

	}

	@Override
	public void init(Map<String, String> config) {

	}

	@Override
	public byte[] end() throws Exception {
		if (returnvalue != null) {
			return Objectutil.convert(returnvalue);
		}
		return null;
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
