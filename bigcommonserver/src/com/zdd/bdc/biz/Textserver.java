package com.zdd.bdc.biz;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Bigdatafileutil;
import com.zdd.bdc.util.Objectutil;

public class Textserver implements Theserverprocess {

	private Map<String, String> readres = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(byte[] b) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(b);
		if ("create".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Map<String, String> cvs = (Map<String, String>) params.get("cvs");
			Map<String, Integer> cvmaxs = (Map<String, Integer>) params.get("cvmaxs");
			for (String column:cvs.keySet()) {
				Path target = Textserver.target(key, namespace, table, column);
				Bigdatafileutil.create(key, target, cvs.get(column).getBytes("UTF-8"), cvmaxs.get(column));
			}
		}
		if ("read".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Vector<String> cols = (Vector<String>) params.get("cols");
			readres = new Hashtable<String, String>(cols.size());
			for (String column:cols) {
				Path target = Textserver.target(key, namespace, table, column);
				byte[] r = Bigdatafileutil.read(key, target);
				if (r!=null) {
					readres.put(column, new String(r,"UTF-8"));
				}
			}
		}
	}

	@Override
	public void process(byte[] b) throws Exception {

	}

	@Override
	public byte[] end() throws Exception {
		if (readres!=null) {
			return Objectutil.convert(readres);
		}
		return null;
	}

	private static Path target(String key, String namespace, String table, String column) throws Exception {
		return Paths.get("bigdata/"+URLEncoder.encode(namespace,"UTF-8")+"/"+URLEncoder.encode(table,"UTF-8")+"/"+URLEncoder.encode(column,"UTF-8")+"/"+Math.abs(key.hashCode())%1000);
	}

}
