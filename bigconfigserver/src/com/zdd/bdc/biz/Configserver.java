package com.zdd.bdc.biz;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Objectutil;

public class Configserver implements Theserverprocess {

	public static Path configfolder = Paths.get("config");

	static {
		if (Files.exists(configfolder) && Files.isDirectory(configfolder)) {
			try {
				Files.walk(configfolder).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = URLDecoder.decode(pathfile.getParent().getFileName().toString(),
									"UTF-8");
							String file = URLDecoder.decode(pathfile.getFileName().toString(), "UTF-8");
							if (file.equals("bigdata")) {
								Bigdataconfig.init(namespace);
							} else if (file.equals("bigindex")) {
								Bigindexconfig.init(namespace);
							}
						} catch (Exception e) {
							System.out.println(new Date() + " ==== System exited when reading ["
									+ pathfile.toAbsolutePath() + "] due to below exception:");
							e.printStackTrace();
							System.exit(1);
						}
					}
				});
			} catch (Exception e) {
				System.out.println(new Date() + " ==== System exited when reading [" + configfolder.toAbsolutePath()
						+ "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static String readconfig(String namespace, String file, String configkey) {
		try {
			if (file.equals("bigdata")) {
				return Bigdataconfig.read(namespace, configkey);
			} else if (file.equals("bigindex")) {
				return Bigindexconfig.read(namespace, configkey);
			} else {
				Path configfile = target(namespace, file);
				List<String> lines = Files.readAllLines(configfile, Charset.forName("UTF-8"));
				for (String line : lines) {
					if (line.indexOf("#") > 0) {
						String encodedkey = line.substring(0, line.indexOf("#"));
						if (URLDecoder.decode(encodedkey, "UTF-8").equals(configkey)) {
							String encodedvalue = "";
							if (line.length() > line.indexOf("#") + 1) {
								encodedvalue = line.substring(line.indexOf("#") + 1);
							}
							return URLDecoder.decode(encodedvalue, "UTF-8");
						}
					}
				}
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, Map<String, Map<String, String>>> returnvalue = new Hashtable<String, Map<String, Map<String, String>>>();

	@Override
	public void init(Map<String, String> config) {

	}

	private static Path target(String namespace, String file) throws Exception {
		return configfolder.resolve(URLEncoder.encode(namespace, "UTF-8") + "/" + URLEncoder.encode(file, "UTF-8"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if (params.get("data") != null) {
			returnvalue = (Map<String, Map<String, Map<String, String>>>) params.get("data");
			if ("read".equals(params.get("action").toString())) {
				Object[] ns = returnvalue.keySet().toArray();
				for (Object nsobj : ns) {
					String namespace = nsobj.toString();
					Object[] files = returnvalue.get(namespace).keySet().toArray();
					for (Object fileobj : files) {
						String file = fileobj.toString();
						Object[] configkeys = returnvalue.get(namespace).get(file).keySet().toArray();
						for (Object configkeyobj : configkeys) {
							String configkey = configkeyobj.toString();
							String configvalue = Configserver.readconfig(namespace, file, configkey);
							if (configvalue == null) {
								returnvalue.get(namespace).get(file).remove(configkey);
								if (returnvalue.get(namespace).get(file).isEmpty()) {
									returnvalue.get(namespace).remove(file);
									if (returnvalue.get(namespace).isEmpty()) {
										returnvalue.remove(namespace);
									}
								}
							} else {
								returnvalue.get(namespace).get(file).put(configkey, configvalue);
							}
						}
					}
				}
			}
		} else {
			throw new Exception("notsupport-" + params.get("action"));
		}
	}

	@Override
	public void requests(byte[] b) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] response() throws Exception {
		return Objectutil.convert(returnvalue);
	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
