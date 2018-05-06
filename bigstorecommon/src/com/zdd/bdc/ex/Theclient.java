package com.zdd.bdc.util;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Theclient {
	
	public static byte[] request(String ip, int port, byte[] start, InputStream ins) throws Exception{
		if (ins!=null&&(start==null||start.length==0)) {
			throw new Exception("inputwithstart");
		}
		if (start==null) {
			start = new byte[0];
		}
		SocketChannel sc = null;
		try{
			sc = SocketChannel.open();
		    sc.connect(new InetSocketAddress(InetAddress.getByName(ip), port));
		    
		    int bs = start.length;
		    ByteBuffer writebb = ByteBuffer.allocate(11+bs);
			writebb.put(String.format ("%011d", bs).getBytes());
			writebb.put(start);
			writebb.flip();
			sc.write(writebb);
			
			if (ins!=null) {
				byte[] readb = new byte[1024];
				bs = 0;
				while((bs=ins.read(readb))!=-1) {
					writebb.clear();
					writebb = ByteBuffer.allocate(11+bs);
					writebb.put(String.format ("%011d", bs).getBytes());
					if (bs!=readb.length) {
						readb=Arrays.copyOf(readb, bs);
					}
					writebb.put(readb);
					writebb.flip();
					sc.write(writebb);
				}
				
			}
			if (bs!=0) {
				writebb.clear();
				writebb = ByteBuffer.allocate(11);
				writebb.put(String.format ("%011d", 0).getBytes());
				writebb.flip();
				sc.write(writebb);
			}
			ByteBuffer readbb = ByteBuffer.allocate(11);
			sc.read(readbb);
			Integer length = Integer.parseInt(new String(readbb.array()));
			readbb.clear();
			readbb = ByteBuffer.allocate(Math.abs(length));
			sc.read(readbb);
			if (length<0) {
				throw new Exception(new String(readbb.array(),"UTF-8"));
			}
			return readbb.array();
		}finally{
			if (sc!=null){
				sc.close();
			}
		}
	}
}