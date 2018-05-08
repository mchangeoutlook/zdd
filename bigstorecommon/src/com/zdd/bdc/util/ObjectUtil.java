package com.zdd.bdc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectUtil {
	public static byte[] convert(Object o) throws Exception {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
	        GZIPOutputStream gzos = new GZIPOutputStream(baos);
	        oos = new ObjectOutputStream(gzos);
		    oos.writeObject(o);
		    oos.flush();
		}finally {
			if (oos !=null) {
				oos.close();
			}
		}
		byte[] res = baos.toByteArray();
		baos.close();
	    return res;
	}
	public static Object convert(byte[] b) throws Exception {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
		    GZIPInputStream gzis = new GZIPInputStream(bais);
		    ois = new ObjectInputStream(gzis);
		    return ois.readObject();
		}finally {
			if (ois !=null) {
				ois.close();
			}
		}
	}
	
}
