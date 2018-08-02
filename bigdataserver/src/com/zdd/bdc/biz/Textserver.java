package com.zdd.bdc.biz;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theclientprocess;
import com.zdd.bdc.util.Bigdatafileutil;
import com.zdd.bdc.util.Objectutil;

public class Textserver implements Theclientprocess {

	private Map<String, String> readres = null;
	private Map<String, Long> incrementres = null;

	private int bigfilehash = 1000;
	
	@Override
	public void init(Map<String, String> config) {
		bigfilehash = Integer.parseInt(config.get("bigfilehash"));
	}
	
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
			for (String column : cvs.keySet()) {
				Path target = target(key, namespace, table, column);
				Bigdatafileutil.create(key, target, cvs.get(column).getBytes("UTF-8"), cvmaxs.get(column));
			}
		} else if ("read".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Vector<String> cols = (Vector<String>) params.get("cols");
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Path target = target(key, namespace, table, column);
				byte[] r = Bigdatafileutil.read(key, target);
				if (r != null) {
					readres.put(column, new String(r, "UTF-8"));
				}
			}
		} else if ("increment".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Map<String, Long> cas = (Map<String, Long>) params.get("cas");
			incrementres = new Hashtable<String, Long>(cas.size());
			for (String column : cas.keySet()) {
				Path target = target(key, namespace, table, column);
				incrementres.put(column, Bigdatafileutil.increment(key, target, cas.get(column)));
			}
		} else if ("modify".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Map<String, String> cvs = (Map<String, String>) params.get("cvs");
			for (String column : cvs.keySet()) {
				Path target = target(key, namespace, table, column);
				Bigdatafileutil.modify(key, target, cvs.get(column).getBytes("UTF-8"));
			}
		} else if ("delete".equals(params.get("action").toString())) {
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			String table = params.get("tb").toString();
			Vector<String> cols = (Vector<String>) params.get("cols");
			readres = new Hashtable<String, String>(cols.size());
			for (String column : cols) {
				Path target = target(key, namespace, table, column);
				Bigdatafileutil.delete(key, target);
			}
		} else {
			throw new Exception("notsupport-"+params.get("action"));
		}
	}

	@Override
	public void process(byte[] b) throws Exception {

	}

	@Override
	public byte[] end() throws Exception {
		if (readres != null) {
			return Objectutil.convert(readres);
		}
		if (incrementres != null) {
			return Objectutil.convert(incrementres);
		}
		return null;
	}

	private Path target(String key, String namespace, String table, String column) throws Exception {
		if (namespace.isEmpty()) {
			throw new Exception("emptyns");
		}
		if (table.isEmpty()) {
			throw new Exception("emptytb");
		}
		if (column.isEmpty()) {
			throw new Exception("emptycol");
		}
		return Paths.get("bigdata/" + URLEncoder.encode(namespace, "UTF-8") + "/" + URLEncoder.encode(table, "UTF-8")
				+ "/" + URLEncoder.encode(column, "UTF-8") + "/" + Math.abs(key.hashCode()) % bigfilehash );
	}

}
