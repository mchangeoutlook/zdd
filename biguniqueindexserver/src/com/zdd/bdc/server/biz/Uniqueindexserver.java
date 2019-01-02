package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Fileuniqueutil;

public class Uniqueindexserver implements Theserverprocess {

	@Override
	public void init(String ip, int port, int thebigfilehash, Map<String, Object> additionalserverconfig) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Object paramobj = Objectutil.convert(param);
		if (paramobj instanceof Map) {
			Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
			if (STATIC.PARAM_ACTION_CREATE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
				String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
				String key = params.get(STATIC.PARAM_KEY_KEY).toString();
				String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
				Object filters = params.get(STATIC.PARAM_FILTERS_KEY);
				String servergroups = (String)params.get(STATIC.PARAM_ADDITIONAL);
				String[] rootrangestr = STATIC.splitfromto(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX));
				int[] rootrange = new int[2];
				rootrange[0] = Integer.parseInt(rootrangestr[0]);
				rootrange[1] = Integer.parseInt(rootrangestr[1]);
				int distributions = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups));
				int capacitykeymax = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX));
				int capacityvalue = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
				Fileuniqueutil.create(namespace, (String)filters, STATIC.tobytes(index), STATIC.tobytes(key), rootrange, capacitykeymax, capacityvalue, distributions);
				return null;
			} else if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
				String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
				String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
				Object filters = params.get(STATIC.PARAM_FILTERS_KEY);
				String servergroups = (String)params.get(STATIC.PARAM_ADDITIONAL);
				String[] rootrangestr = STATIC.splitfromto(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX));
				int[] rootrange = new int[2];
				rootrange[0] = Integer.parseInt(rootrangestr[0]);
				rootrange[1] = Integer.parseInt(rootrangestr[1]);
				int distributions = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups));
				int capacitykeymax = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX));
				int capacityvalue = Integer.parseInt(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(servergroups+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
				return Objectutil.convert(STATIC.tostring(Fileuniqueutil.read(namespace, (String)filters, STATIC.tobytes(index), rootrange, capacitykeymax, capacityvalue, distributions)));
			} else {
				throw new Exception("notsupport-" + params.get(STATIC.PARAM_ACTION_KEY));
			}
		} else {
			return null;
		}
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		Object paramobj = Objectutil.convert(param);
		if (paramobj instanceof Map) {
			return null;
		} else {
			String[] namespacefilterorleaf = STATIC.splitenc((String)paramobj);
			if (namespacefilterorleaf.length==2) {
				return Files.newInputStream(Fileuniqueutil.rootfile(namespacefilterorleaf[0], namespacefilterorleaf[1]));
			} else if (namespacefilterorleaf.length==3) {
				return Files.newInputStream(Fileuniqueutil.leafolder(namespacefilterorleaf[0], namespacefilterorleaf[1]).resolve(namespacefilterorleaf[2]));
			} else {
				return Files.newInputStream(Fileuniqueutil.collisionfolder(namespacefilterorleaf[0], namespacefilterorleaf[1]).resolve(namespacefilterorleaf[3]));
			}
		}
	}

	@Override
	public int requestoutputbytes(byte[] param) throws Exception {
		Object paramobj = Objectutil.convert(param);
		if (paramobj instanceof Map) {
			return -1;
		} else {
			String[] namespacefilterorleaf = STATIC.splitenc((String)paramobj);
			if (namespacefilterorleaf.length==2) {
				return 32;
			} else {
				int capacitykeymax = Integer.parseInt(Configclient.getinstance(namespacefilterorleaf[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(Configclient.getinstance(namespacefilterorleaf[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(namespacefilterorleaf[1])+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX));
				int capacityvalue = Integer.parseInt(Configclient.getinstance(namespacefilterorleaf[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(Configclient.getinstance(namespacefilterorleaf[0], STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX).read(namespacefilterorleaf[1])+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX));
				return capacitykeymax+capacityvalue;
			}
		}
	}
}
