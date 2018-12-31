package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Filedatautil;

public class Dataserver implements Theserverprocess {

	private int bigfilehash = 10000;

	@Override
	public void init(String ip, int port, int thebigfilehash, Map<String, Object> additionalserverconfig) {
		bigfilehash = thebigfilehash;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
		if (STATIC.PARAM_ACTION_CREATE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(STATIC.PARAM_COLUMNVALUES_KEY);
			Map<String, Integer> cvmaxs = (Map<String, Integer>) params.get(STATIC.PARAM_COLUMNMAXVALUES_KEY);
			for (String column : cvs.keySet()) {
				Filedatautil.create(key, cvs.get(column), cvmaxs.get(column), namespace, table, column, bigfilehash);
			}
			return null;
		} else if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(STATIC.PARAM_COLUMNS_KEY);
			Map<String, String> readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				String result = Filedatautil.read(key, namespace, table, column, bigfilehash);
				if (result != null) {
					readres.put(column, result);
				}
			}
			return Objectutil.convert(readres);
		} else if (STATIC.PARAM_ACTION_INCREMENT.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, Long> cas = (Map<String, Long>) params.get(STATIC.PARAM_COLUMNAMOUNTS_KEY);
			Map<String, Long> incrementres = new Hashtable<String, Long>(cas.size());
			for (String column : cas.keySet()) {
				long result = Filedatautil.increment(key, cas.get(column), namespace, table, column, bigfilehash);
				incrementres.put(column, result);
			}
			return Objectutil.convert(incrementres);
		} else if (STATIC.PARAM_ACTION_MODIFY.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(STATIC.PARAM_COLUMNVALUES_KEY);
			for (String column : cvs.keySet()) {
				Filedatautil.modify(key, cvs.get(column), namespace, table, column, bigfilehash);
			}
			return null;
		} else if (STATIC.PARAM_ACTION_DELETE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(STATIC.PARAM_COLUMNS_KEY);
			Map<String, String> readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Filedatautil.delete(key, namespace, table, column, bigfilehash);
			}
			return Objectutil.convert(readres);
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
