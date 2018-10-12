package com.zdd.bdc.util;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Vector;

import com.zdd.bdc.biz.Bigclient;

public class Bigindexfileutil {
	private static String[] synchorizedfile = null;

	public static void initonlyonce(int bigfilehash) {
		if (synchorizedfile == null) {
			synchorizedfile = new String[bigfilehash];
			for (int i = 0; i < bigfilehash; i++) {
				synchorizedfile[i] = String.valueOf(i);
			}
		}
	}

	public static Vector<String> read(String index, Path target, int numofdata) throws Exception {
		synchronized (synchorizedfile[Integer.parseInt(target.getFileName().toString())]) {
			if (index.isEmpty()) {
				throw new Exception("emptyindex");
			}
			Vector<String> keys = new Vector<String>(numofdata);
			if (Files.exists(target)) {
				BufferedReader br = null;
				try {
					br = Files.newBufferedReader(target, Charset.forName(STATIC.CHARSET_DEFAULT));
					String line = br.readLine();
					while (line != null && !line.trim().isEmpty()) {
						if (STATIC.commentget(line)==null) {
							String[] indexkey = STATIC.keyval(line);
							if (indexkey[0].equals(index)) {
								keys.add(indexkey[1]);
							}
						}
						line = br.readLine();
					}
				} finally {
					if (br != null) {
						br.close();
					}
				}
			}
			return keys;
		}
	}

	public static String readunique(String index, Path target) throws Exception {
		synchronized (synchorizedfile[Integer.parseInt(target.getFileName().toString())]) {
			if (index.isEmpty()) {
				throw new Exception("emptyindex");
			}
			if (Files.exists(target)) {
				BufferedReader br = null;
				try {
					br = Files.newBufferedReader(target, Charset.forName(STATIC.CHARSET_DEFAULT));
					String line = br.readLine();
					while (line != null && !line.trim().isEmpty()) {
						if (STATIC.commentget(line)==null) {
							String[] indexkey = STATIC.keyval(line);
							if (indexkey[0].equals(index)) {
								return indexkey[1];
							}
						}
						line = br.readLine();
					}
				} finally {
					if (br != null) {
						br.close();
					}
				}
			}
			return "";
		}
	}

	public static void create(String index, Path target, String key) throws Exception {
		synchronized (synchorizedfile[Integer.parseInt(target.getFileName().toString())]) {
			if (index.isEmpty()) {
				throw new Exception("emptyindex");
			}
			if (key.isEmpty()) {
				throw new Exception("emptykey");
			}
			byte[] bline = (STATIC.keyval(index,key)
					+ System.lineSeparator()).getBytes(STATIC.CHARSET_DEFAULT);
			ByteBuffer bb = ByteBuffer.allocate(bline.length);
			bb.put(bline);
			write(target, bb);
		}
	}

	public static void create(String index, Path target, String key, String version) throws Exception {
		synchronized (synchorizedfile[Integer.parseInt(target.getFileName().toString())]) {
			if (index.isEmpty()) {
				throw new Exception("emptyindex");
			}
			if (key.isEmpty()) {
				throw new Exception("emptykey");
			}
			if (version.isEmpty()) {
				throw new Exception("emptyversion");
			}
			boolean isold = false;
			if (Files.exists(target)) {
				BufferedReader br = null;
				String firstline = null;
				try {
					br = Files.newBufferedReader(target, Charset.forName(STATIC.CHARSET_DEFAULT));
					firstline = br.readLine();
				} finally {
					if (br!=null) {
						br.close();
					}
				}
				if (firstline == null) {
					isold = true;
				} else {
					if (STATIC.commentget(firstline)==null) {
						throw new Exception("emptyoldversion");
					} else {
						if (version.compareTo(STATIC.commentget(firstline)) > 0) {
							isold = true;
						}
					}
				}

			} else {
				isold = true;
			}
			if (isold) {
				Files.write(target, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.SYNC);
				byte[] bline = (STATIC.commentput(version)
						+ System.lineSeparator()).getBytes(STATIC.CHARSET_DEFAULT);
				ByteBuffer bb = ByteBuffer.allocate(bline.length);
				bb.put(bline);
				write(target, bb);
			}
			byte[] bline = (STATIC.keyval(index, key)
					+ System.lineSeparator()).getBytes(STATIC.CHARSET_DEFAULT);
			ByteBuffer bb = ByteBuffer.allocate(bline.length);
			bb.put(bline);
			write(target, bb);
		}
	}

	public static void createunique(String index, Path target, String key) throws Exception {
		synchronized (synchorizedfile[Integer.parseInt(target.getFileName().toString())]) {
			if (index.isEmpty()) {
				throw new Exception("emptyindex");
			}
			if (key.isEmpty()) {
				throw new Exception("emptykey");
			}
			if (readunique(index, target).isEmpty()) {
				byte[] bline = (STATIC.keyval(index, key)
						+ System.lineSeparator()).getBytes(STATIC.CHARSET_DEFAULT);
				ByteBuffer bb = ByteBuffer.allocate(bline.length);
				bb.put(bline);
				write(target, bb);
			} else {
				throw new Exception("duplicate");
			}
		}
	}

	private static void write(Path target, ByteBuffer bb) throws Exception {
		if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
			Files.createDirectories(target.getParent());
		}
		Files.write(target, bb.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
	}

	public static Path target(String index, long pagenum, Vector<String> filters, String namespace, int bigfilehash)
			throws Exception {
		if (namespace.isEmpty()) {
			throw new Exception("emptyns");
		}
		Vector<String> filtersandpagenum = new Vector<String>(filters.size()+1);
		if (filters != null && !filters.isEmpty()) {
			Collections.sort(filters);
			for (String f : filters) {
				filtersandpagenum.add(f);
			}
		}
		filtersandpagenum.add(String.valueOf(pagenum));
		String[] fp = new String[filtersandpagenum.size()];
		filtersandpagenum.toArray(fp);
		return STATIC.LOCAL_DATAFOLDER.resolve(URLEncoder.encode(namespace, STATIC.CHARSET_DEFAULT))
				.resolve(Bigclient.distributebigindexserveri(namespace, pagenum, index))
				.resolve(STATIC.splitenc(fp))
				.resolve(String.valueOf(Math.abs(index.hashCode()) % bigfilehash));
	}

}
