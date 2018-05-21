package com.zdd.bdc.util;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Bigindexfileutil {

	public static List<String> read(String index, Path target) throws Exception{
		List<String> keys = new ArrayList<String>(100);
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
		byte[] bline = (URLEncoder.encode(index, "UTF-8")+"#"+key+System.lineSeparator()).getBytes();
		ByteBuffer bb = ByteBuffer.allocate(bline.length);
		bb.put(bline);
		write(target, bb);
	}

	private static void write(Path target, ByteBuffer bb) throws Exception{
		Files.write(target, bb.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
	}
}
