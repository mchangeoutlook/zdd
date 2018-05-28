package com.zdd.bdc.util;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

public class Bigindexfileutil {

	public static Vector<String> read(String index, Path target) throws Exception{
		if (index.isEmpty()) {
			throw new Exception("emptyindex");
		}
		Vector<String> keys = new Vector<String>(100);
		BufferedReader br = Files.newBufferedReader(target, Charset.forName("UTF-8"));
		String line = br.readLine();
		while (line != null){
			if (line.startsWith(URLEncoder.encode(index, "UTF-8")+"#")){
				keys.add(line.split("#")[1]);
			}
			line = br.readLine();
		}
		
		return keys;
	}
	
	public static synchronized void create(String index, Path target, String key) throws Exception{
		if (index.isEmpty()) {
			throw new Exception("emptyindex");
		}
		if (key.isEmpty()) {
			throw new Exception("emptykey");
		}
		byte[] bline = (URLEncoder.encode(index, "UTF-8")+"#"+key+System.lineSeparator()).getBytes();
		ByteBuffer bb = ByteBuffer.allocate(bline.length);
		bb.put(bline);
		write(target, bb);
	}

	public static synchronized void createunique(String index, Path target, String key) throws Exception{
		if (index.isEmpty()) {
			throw new Exception("emptyindex");
		}
		if (key.isEmpty()) {
			throw new Exception("emptykey");
		}
		if (read(index, target).isEmpty()) {
			byte[] bline = (URLEncoder.encode(index, "UTF-8")+"#"+key+System.lineSeparator()).getBytes();
			ByteBuffer bb = ByteBuffer.allocate(bline.length);
			bb.put(bline);
			write(target, bb);
		} else {
			throw new Exception("duplicate");
		}
	}

	private static void write(Path target, ByteBuffer bb) throws Exception{
		if (!Files.exists(target)&&target.getParent()!=null&&!Files.exists(target.getParent())) {
			Files.createDirectories(target.getParent());
		}
		Files.write(target, bb.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
	}
}
