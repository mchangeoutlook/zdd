package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Fileconfigutil;
import com.zdd.bdc.server.util.Monitor;

public class Configserver implements Theserverprocess {

	static {
		final Vector<String> configfiles = new Vector<String>();
		if (Files.exists(STATIC.LOCAL_CONFIGFOLDER) && Files.isDirectory(STATIC.LOCAL_CONFIGFOLDER)) {
			try {
				Files.walk(STATIC.LOCAL_CONFIGFOLDER).filter(Files::isRegularFile).forEach(pathfile -> {
					if (!pathfile.getFileName().toString().startsWith(".")) {
						configfiles.add(pathfile.toString());
					}
				});
			} catch (Exception e) {
				System.out.println(new Date() + " ==== System exited when reading ["
						+ STATIC.LOCAL_CONFIGFOLDER.toAbsolutePath() + "] due to below exception:");
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (configfiles.isEmpty()) {
			System.out.println(new Date() + " ==== System exited due to missing config files under folder ["+STATIC.LOCAL_CONFIGFOLDER.toString()+"]");
			System.exit(1);
		} else {
			for (String configfile:configfiles) {
				System.out.println(configfile);
			}
			System.out.println(new Date() + " ==== discovered "+configfiles.size()+" config files under folder ["+STATIC.LOCAL_CONFIGFOLDER.toString()+"]");
		}
	}
	
	public static String readconfig(String namespace, String file, String configkey) throws Exception {
		if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGDATA)) {
			return Bigdataconfig.read(namespace, configkey);
		} else if (file.equals(STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX)) {
			return Bigunindexconfig.read(namespace, configkey);
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
		if (params.get(STATIC.PARAM_CLIENTSTATIPORTFOLDER)!=null) {
			String[] clientspaceiportfolder = STATIC.splitenc(params.get(STATIC.PARAM_CLIENTSTATIPORTFOLDER).toString());
			String serverspacehint = clientspaceiportfolder[0];
			String ip = clientspaceiportfolder[1];
			String port = clientspaceiportfolder[2];
			String folder = clientspaceiportfolder[3];
			if (STATIC.REMOTE_CONFIGVAL_PENDING.equals(readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING, 
					STATIC.splitiport(ip, port)))){
				throw new Exception(STATIC.SHUTDOWN);
			}
			if (serverspacehint!=null&&!serverspacehint.trim().isEmpty()) {
				Monitor.qqemail(Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE, "notify.sender"), 
						Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE, "notify.receiver"), "client stat", 
						folder+ip+port+"<br>"+serverspacehint.replaceAll(System.lineSeparator(), "<br>"));
			}
			Monitor.updateclientime(folder+ip+port);
		}
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

	@Override
	public int requestoutputbytes(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
