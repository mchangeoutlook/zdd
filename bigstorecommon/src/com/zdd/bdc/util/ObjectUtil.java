package com.zdd.bdc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectUtil {
	public static byte[] convert(Object o) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);
	    oos.writeObject(o);
	    oos.flush();
	    oos.close();
		return baos.toByteArray();
	}
	public static Object convert(byte[] b) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
	    GZIPInputStream gzis = new GZIPInputStream(bais);
	    ObjectInputStream ois = new ObjectInputStream(gzis);
	    return ois.readObject();
	}
	
}
