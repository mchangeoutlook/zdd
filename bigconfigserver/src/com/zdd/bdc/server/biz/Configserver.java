package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configserver implements Theserverprocess {

	static {
		if (Files.exists(STATIC.LOCAL_CONFIGFOLDER) && Files.isDirectory(STATIC.LOCAL_CONFIGFOLDER)) {
			try {
				Files.walk(STATIC.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						try {
							String namespace = pathfile.getParent().getFileName().toString();
							String file = pathfile.getFileName().toString();
							if (file.equals(STATIC.REMOTE_CONFIG_BIGDATA)) {
								Bigdataconfig.init(namespace);
							} else if (file.equals(STATIC.REMOTE_CONFIG_BIGPAGEDINDEX)) {
								Bigpagedindexconfig.init(namespace);
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

	public static String readconfig(String namespace, String file, String configkey) throws Exception {
		if (file.equals(STATIC.REMOTE_CONFIG_BIGDATA)) {
			return Bigdataconfig.read(namespace, configkey);
		} else if (file.equals(STATIC.REMOTE_CONFIG_BIGPAGEDINDEX)) {
			return Bigpagedindexconfig.read(namespace, configkey);
		} else {
			return Fileconfigutil.readone(configkey, namespace, file);
		}
	}

	@Override
	public void init(String ip, int port, int bigfilehash, Map<String, Object> additionalserverconfig) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
		if (params.get(STATIC.PARAM_DATA_KEY) != null) {
			Map<String, Map<String, Map<String, String>>> returnvalue = (Map<String, Map<String, Map<String, String>>>) params.get(STATIC.PARAM_DATA_KEY);
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
			return Objectutil.convert(returnvalue);
		} else {
			throw new Exception("notsupport-" + params.get(STATIC.PARAM_ACTION_KEY));
		}
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
