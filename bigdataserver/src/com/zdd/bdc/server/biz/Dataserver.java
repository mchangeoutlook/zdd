package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Filekvutil;

public class Dataserver implements Theserverprocess {

	private Map<String, String> readres = null;
	private Map<String, Long> incrementres = null;

	private int bigfilehash = 1000;

	@Override
	public void init(int thebigfilehash) {
		bigfilehash = thebigfilehash;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if (CS.PARAM_ACTION_CREATE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(CS.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(CS.PARAM_COLUMNVALUES_KEY);
			Map<String, Integer> cvmaxs = (Map<String, Integer>) params.get(CS.PARAM_COLUMNMAXVALUES_KEY);
			for (String column : cvs.keySet()) {
				Filekvutil.datacreate(key, cvs.get(column), cvmaxs.get(column), namespace, table, column, bigfilehash);
			}
		} else if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(CS.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(CS.PARAM_COLUMNS_KEY);
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				String result = Filekvutil.dataread(key, namespace, table, column, bigfilehash);
				if (result != null) {
					readres.put(column, result);
				}
			}
		} else if (CS.PARAM_ACTION_INCREMENT.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(CS.PARAM_TABLE_KEY).toString();
			Map<String, Long> cas = (Map<String, Long>) params.get(CS.PARAM_COLUMNAMOUNTS_KEY);
			incrementres = new Hashtable<String, Long>(cas.size());
			for (String column : cas.keySet()) {
				long result = Filekvutil.dataincrement(key, cas.get(column), namespace, table, column, bigfilehash);
				incrementres.put(column, result);
			}
		} else if (CS.PARAM_ACTION_MODIFY.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(CS.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(CS.PARAM_COLUMNVALUES_KEY);
			for (String column : cvs.keySet()) {
				Filekvutil.datamodify(key, cvs.get(column), namespace, table, column, bigfilehash);
			}
		} else if (CS.PARAM_ACTION_DELETE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(CS.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(CS.PARAM_COLUMNS_KEY);
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Filekvutil.datadelete(key, namespace, table, column, bigfilehash);
			}
		} else {
			throw new Exception("notsupport-" + params.get(CS.PARAM_ACTION_KEY));
		}
	}

	@Override
	public byte[] response() throws Exception {
		if (readres != null) {
			return Objectutil.convert(readres);
		}
		if (incrementres != null) {
			return Objectutil.convert(incrementres);
		}
		return Objectutil.convert(new Hashtable<String, String>());
	}

	@Override
	public void requests(byte[] b) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
