package com.zdd.bdc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.zdd.bdc.biz.Fileclient;
import com.zdd.bdc.biz.Textclient;

public class Testclient {
	public static void main(String[] s) throws IOException, Exception {
		System.out.println("["+Textclient.create()+"]");/*
		Map<String, List<Map<String,Object>>> a = new HashMap<String, List<Map<String,Object>>>();
		   Map<String, Object> b = new HashMap<String, Object>();
		   b.put("1111", "11111你好");
		   b.put("2222", 2);
		   List<Map<String,Object>> c = new ArrayList<Map<String,Object>>();
		   c.add(b);
		   a.put("3333", c);
		   
		   ObjectOutputStream objectOutputStream = null;
		  // try {
		   ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		         GZIPOutputStream gzipOutputStream = new GZIPOutputStream(arrayOutputStream);
		          objectOutputStream = new ObjectOutputStream(gzipOutputStream);
		      objectOutputStream.writeObject(a);
		      objectOutputStream.flush();
		      objectOutputStream.close();
		     //return new String(base64.encode(arrayOutputStream.toByteArray()));
		      System.out.println(new String(arrayOutputStream.toByteArray()));
		      //System.out.println(new ObjectMapper().writeValueAsBytes(a).length);
		      //System.out.println(new ObjectMapper().writeValueAsString(a));
		      byte[] bb = arrayOutputStream.toByteArray();
		      System.out.println(bb.length);
		      ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bb);
		      GZIPInputStream gzipInputStream = new GZIPInputStream(arrayInputStream);
		      ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
		      Map<String, List<Map<String,Object>>> aa = (Map<String, List<Map<String,Object>>>)objectInputStream.readObject();
		       
		      System.out.println("["+aa.get("3333").get(0).get("1111")+"]");
		   //}finally {
			   //objectOutputStream.close();
		 //  }*/
	}
}
