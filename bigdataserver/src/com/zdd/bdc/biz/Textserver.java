package com.zdd.bdc.biz;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Bigdatafileutil;
import com.zdd.bdc.util.Objectutil;
import com.zdd.bdc.util.STATIC;

public class Textserver implements Theserverprocess {

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
		if (STATIC.PARAM_ACTION_CREATE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(STATIC.PARAM_COLUMNVALUES_KEY);
			Map<String, Integer> cvmaxs = (Map<String, Integer>) params.get(STATIC.PARAM_COLUMNMAXVALUES_KEY);
			for (String column : cvs.keySet()) {
				Path target = Bigdatafileutil.target(key, namespace, table, column, bigfilehash);
				Bigdatafileutil.create(key, target, cvs.get(column).getBytes("UTF-8"), cvmaxs.get(column));
			}
		} else if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(STATIC.PARAM_COLUMNS_KEY);
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Path target = Bigdatafileutil.target(key, namespace, table, column, bigfilehash);
				byte[] r = Bigdatafileutil.read(key, target);
				if (r != null) {
					readres.put(column, new String(r, "UTF-8"));
				}
			}
		} else if (STATIC.PARAM_ACTION_INCREMENT.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, Long> cas = (Map<String, Long>) params.get(STATIC.PARAM_COLUMNAMOUNTS_KEY);
			incrementres = new Hashtable<String, Long>(cas.size());
			for (String column : cas.keySet()) {
				Path target = Bigdatafileutil.target(key, namespace, table, column, bigfilehash);
				incrementres.put(column, Bigdatafileutil.increment(key, target, cas.get(column)));
			}
		} else if (STATIC.PARAM_ACTION_MODIFY.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Map<String, String> cvs = (Map<String, String>) params.get(STATIC.PARAM_COLUMNVALUES_KEY);
			for (String column : cvs.keySet()) {
				Path target = Bigdatafileutil.target(key, namespace, table, column, bigfilehash);
				Bigdatafileutil.modify(key, target, cvs.get(column).getBytes("UTF-8"));
			}
		} else if (STATIC.PARAM_ACTION_DELETE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			String table = params.get(STATIC.PARAM_TABLE_KEY).toString();
			Vector<String> cols = (Vector<String>) params.get(STATIC.PARAM_COLUMNS_KEY);
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Path target = Bigdatafileutil.target(key, namespace, table, column, bigfilehash);
				Bigdatafileutil.delete(key, target);
			}
		} else {
			throw new Exception("notsupport-"+params.get(STATIC.PARAM_ACTION_KEY));
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
		return null;
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
