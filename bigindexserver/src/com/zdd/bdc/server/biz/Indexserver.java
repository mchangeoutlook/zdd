package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Filekvutil;

public class Indexserver implements Theserverprocess {

	private int bigfilehash = 0;

	@Override
	public void init(String ip, int port, int thebigfilehash, Map<String, Object> additionalserverconfig) {
		bigfilehash = thebigfilehash;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
		if (STATIC.PARAM_ACTION_CREATE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(STATIC.PARAM_FILTERS_KEY);
			if (params.get(STATIC.PARAM_VERSION_KEY)!=null&&!params.get(STATIC.PARAM_VERSION_KEY).toString().trim().isEmpty()) {
				Filekvutil.indexversion(params.get(STATIC.PARAM_VERSION_KEY).toString(), index, key, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
						.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS))));
			} else {
				if (Long.parseLong(filters.get(0))==STATIC.PAGENUM_UNIQUE) {
					Filekvutil.index(index, key, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
							.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS))));
				} else {
					Filekvutil.indexes(index, key, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
							.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS))));
				}
			}
			return null;
		} else if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(STATIC.PARAM_FILTERS_KEY);
			if (Long.parseLong(filters.get(0))==STATIC.PAGENUM_UNIQUE) {
				return Objectutil.convert(Filekvutil.index(index, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
						.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
			} else {
				return Objectutil.convert(Filekvutil.indexes(index, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
						.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
			}
		} else if (STATIC.PARAM_ACTION_INCREMENT.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(STATIC.PARAM_FILTERS_KEY);
			return Objectutil.convert(Filekvutil.indexincrement(index, filters, bigfilehash, Filekvutil.indexfolder(namespace, filters, index, Integer.parseInt(Configclient
					.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(STATIC.REMOTE_CONFIGKEY_MAXINDEXSERVERS)))));
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
