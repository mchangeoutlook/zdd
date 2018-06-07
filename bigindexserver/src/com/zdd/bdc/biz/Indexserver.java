package com.zdd.bdc.biz;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.ex.Theserverprocess;
import com.zdd.bdc.util.Bigindexfileutil;
import com.zdd.bdc.util.Objectutil;

public class Indexserver implements Theserverprocess {

	private Vector<String> readres = null;
	private String unique = null;

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
			String index = params.get("index").toString();
			String key = params.get("key").toString();
			String namespace = params.get("ns").toString();
			Vector<String> filters = (Vector<String>) params.get("filters");
			if (params.get("pagenum") == null) {
				Path target = target(index, -1, filters, namespace);
				Bigindexfileutil.createunique(index, target, key);
			} else {
				long pagenum = Long.parseLong(params.get("pagenum").toString());
				Path target = target(index, pagenum, filters, namespace);
				Bigindexfileutil.create(index, target, key);
			}
		} else if ("read".equals(params.get("action").toString())) {
			String index = params.get("index").toString();
			String namespace = params.get("ns").toString();
			Vector<String> filters = (Vector<String>) params.get("filters");
			if (params.get("pagenum") == null) {
				Path target = target(index, -1, filters, namespace);
				Vector<String> res = Bigindexfileutil.read(index, target);
				unique = "";
				if (!res.isEmpty()) {
					unique = res.get(0);
				}
			} else {
				long pagenum = Long.parseLong(params.get("pagenum").toString());
				Path target = target(index, pagenum, filters, namespace);
				readres = Bigindexfileutil.read(index, target);
			}
		} else {
			throw new Exception("notsupport-" + params.get("action"));
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
		if (unique != null) {
			return Objectutil.convert(unique);
		}
		return null;
	}

	private Path target(String index, long pagenum, Vector<String> filters, String namespace) throws Exception {
		if (namespace.isEmpty()) {
			throw new Exception("emptyns");
		}
		String s = "";
		if (filters != null && !filters.isEmpty()) {
			Collections.sort(filters);
			for (String f : filters) {
				s += URLEncoder.encode(f, "UTF-8") + "#";
			}
		}
		return Paths.get("bigindex/" + URLEncoder.encode(namespace, "UTF-8") + "/" + pagenum + "#" + s + "/"
				+ Math.abs(index.hashCode()) % bigfilehash);
	}

}
