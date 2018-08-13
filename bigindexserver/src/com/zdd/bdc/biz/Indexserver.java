package com.zdd.bdc.biz;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Bigindexfileutil;
import com.zdd.bdc.util.Objectutil;
import com.zdd.bdc.util.STATIC;

public class Indexserver implements Theserverprocess {

	private Vector<String> readres = null;
	private String unique = null;

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
			String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
			String key = params.get(STATIC.PARAM_KEY_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(STATIC.PARAM_FILTERS_KEY);
			if (params.get(STATIC.PARAM_PAGENUM_KEY) == null) {
				Path target = Bigindexfileutil.target(index, STATIC.PAGENUM_UNIQUEINDEX, filters, namespace, bigfilehash);
				Bigindexfileutil.createunique(index, target, key);
			} else {
				long pagenum = Long.parseLong(params.get(STATIC.PARAM_PAGENUM_KEY).toString());
				Path target = Bigindexfileutil.target(index, pagenum, filters, namespace, bigfilehash);
				Bigindexfileutil.create(index, target, key);
			}
		} else if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
			String index = params.get(STATIC.PARAM_INDEX_KEY).toString();
			String namespace = params.get(STATIC.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(STATIC.PARAM_FILTERS_KEY);
			if (params.get(STATIC.PARAM_PAGENUM_KEY) == null) {
				Path target = Bigindexfileutil.target(index, STATIC.PAGENUM_UNIQUEINDEX, filters, namespace, bigfilehash);
				Vector<String> res = Bigindexfileutil.read(index, target);
				unique = "";
				if (!res.isEmpty()) {
					unique = res.get(0);
				}
			} else {
				long pagenum = Long.parseLong(params.get(STATIC.PARAM_PAGENUM_KEY).toString());
				Path target = Bigindexfileutil.target(index, pagenum, filters, namespace, bigfilehash);
				readres = Bigindexfileutil.read(index, target);
			}
		} else {
			throw new Exception("notsupport-" + params.get(STATIC.PARAM_ACTION_KEY));
		}
	}

	@Override
	public void requests(byte[] b) throws Exception {

	}

	@Override
	public byte[] response() throws Exception {
		if (readres != null) {
			return Objectutil.convert(readres);
		}
		if (unique != null) {
			return Objectutil.convert(unique);
		}
		return null;
	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
}
