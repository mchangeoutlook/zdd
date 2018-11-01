package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.SS;

public class Configserver implements Theserverprocess {

	static {
		if (Files.exists(SS.LOCAL_CONFIGFOLDER) && Files.isDirectory(SS.LOCAL_CONFIGFOLDER)) {
			try {
				Files.walk(SS.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = pathfile.getParent().getFileName().toString();
							String file = pathfile.getFileName().toString();
							if (file.equals(CS.REMOTE_CONFIG_BIGDATA)) {
								Bigdataconfig.init(namespace);
							} else if (file.equals(CS.REMOTE_CONFIG_BIGINDEX)) {
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
						+ SS.LOCAL_CONFIGFOLDER.toAbsolutePath() + "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static String readconfig(String namespace, String file, String configkey) throws Exception {
		if (file.equals(CS.REMOTE_CONFIG_BIGDATA)) {
			return Bigdataconfig.read(namespace, configkey);
		} else if (file.equals(CS.REMOTE_CONFIG_BIGINDEX)) {
			return Bigindexconfig.read(namespace, configkey);
		} else {
			return Filekvutil.config(configkey, namespace, file);
		}
	}

	private Map<String, Map<String, Map<String, String>>> returnvalue = new Hashtable<String, Map<String, Map<String, String>>>();

	@Override
	public void init(String ip, int port, int bigfilehash) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if (params.get(CS.PARAM_DATA_KEY) != null) {
			returnvalue = (Map<String, Map<String, Map<String, String>>>) params.get(CS.PARAM_DATA_KEY);
			if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
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
			throw new Exception("notsupport-" + params.get(CS.PARAM_ACTION_KEY));
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
