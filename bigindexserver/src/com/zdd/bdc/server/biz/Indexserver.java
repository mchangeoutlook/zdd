package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.SS;

public class Indexserver implements Theserverprocess {

	private Vector<String> readres = null;
	private String unique = null;

	private int bigfilehash = 1000;

	@Override
	public void init(String ip, int port, int thebigfilehash) {
		bigfilehash = thebigfilehash;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void request(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if (CS.PARAM_ACTION_CREATE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String index = params.get(CS.PARAM_INDEX_KEY).toString();
			String key = params.get(CS.PARAM_KEY_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(CS.PARAM_FILTERS_KEY);
			Long pagenum = null;
			if (params.get(CS.PARAM_PAGENUM_KEY) != null) {
				pagenum = Long.parseLong(params.get(CS.PARAM_PAGENUM_KEY).toString());
			} else {
				//do nothing
			}
			if (params.get(CS.PARAM_VERSION_KEY)!=null&&!params.get(CS.PARAM_VERSION_KEY).toString().trim().isEmpty()) {
				Filekvutil.indexversion(params.get(CS.PARAM_VERSION_KEY).toString(), index, key, pagenum, filters, bigfilehash, indexfolder(namespace, pagenum, index));
			} else {
				Filekvutil.index(index, key, pagenum, filters, bigfilehash, indexfolder(namespace, pagenum, index));
			}
		} else if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
			String index = params.get(CS.PARAM_INDEX_KEY).toString();
			String namespace = params.get(CS.PARAM_NAMESPACE_KEY).toString();
			Vector<String> filters = (Vector<String>) params.get(CS.PARAM_FILTERS_KEY);
			if (params.get(CS.PARAM_PAGENUM_KEY) == null) {
				unique = Filekvutil.index(index, filters, bigfilehash, indexfolder(namespace, null, index));
			} else {
				long pagenum = Long.parseLong(params.get(CS.PARAM_PAGENUM_KEY).toString());
				int numofdata = Integer.parseInt(params.get(CS.PARAM_NUMOFDATA).toString());
				readres = Filekvutil.indexes(index, numofdata, pagenum, filters, bigfilehash, indexfolder(namespace, pagenum, index));
			}
		} else {
			throw new Exception("notsupport-" + params.get(CS.PARAM_ACTION_KEY));
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
		return Objectutil.convert(new Vector<String>());
	}

	@Override
	public InputStream responses() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private static Path indexfolder(String namespace, Long pagenum, String index) {
		return SS.LOCAL_DATAFOLDER.resolve(namespace)
		.resolve(Bigclient.distributebigindexserveri(namespace, pagenum, index));
	}
}
