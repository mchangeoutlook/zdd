package com.zdd.bdc.biz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Objectutil;
import com.zdd.bdc.util.STATIC;

public class Configserver implements Theserverprocess {

	static {
		if (Files.exists(STATIC.LOCAL_CONFIGFOLDER) && Files.isDirectory(STATIC.LOCAL_CONFIGFOLDER)) {
			try {
				Files.walk(STATIC.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = URLDecoder.decode(pathfile.getParent().getFileName().toString(),
									STATIC.CHARSET_DEFAULT);
							String file = URLDecoder.decode(pathfile.getFileName().toString(), STATIC.CHARSET_DEFAULT);
							if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGDATA)) {
								Bigdataconfig.init(namespace);
							} else if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGINDEX)) {
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
				System.out.println(new Date() + " ==== System exited when reading ["
						+ STATIC.LOCAL_CONFIGFOLDER.toAbsolutePath() + "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static String readconfig(String namespace, String file, String configkey) {
		if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGDATA)) {
			return Bigdataconfig.read(namespace, configkey);
		} else if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGINDEX)) {
			return Bigindexconfig.read(namespace, configkey);
		} else {
			BufferedReader br = null;
			try {
				Path configfile = targetconfigfile(namespace, file);
				br = Files.newBufferedReader(configfile, Charset.forName(STATIC.CHARSET_DEFAULT));
				String line = br.readLine();
				while (line != null) {
					if (STATIC.commentget(line) == null) {
						String[] keyval = STATIC.keyval(line);
						if (keyval[0].equals(configkey)) {
							return keyval[1];
						}
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (Exception e) {
						// do nothing
					}
				}
			}
		}
	}

	private Map<String, Map<String, Map<String, String>>> returnvalue = new Hashtable<String, Map<String, Map<String, String>>>();

	@Override
	public void init(int bigfilehash) {

	}

	private static Path targetconfigfile(String namespace, String file) throws Exception {
		return STATIC.LOCAL_CONFIGFOLDER.resolve(URLEncoder.encode(namespace, STATIC.CHARSET_DEFAULT) + "/"
				+ URLEncoder.encode(file, STATIC.CHARSET_DEFAULT));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if (params.get(STATIC.PARAM_DATA_KEY) != null) {
			returnvalue = (Map<String, Map<String, Map<String, String>>>) params.get(STATIC.PARAM_DATA_KEY);
			if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
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
			throw new Exception("notsupport-" + params.get(STATIC.PARAM_ACTION_KEY));
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
