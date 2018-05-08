package com.zdd.bdc.ex;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class Theserver {
	
	public static void startblocking(int port, final Class<?> c) throws Exception{
		try{
			c.asSubclass(Theserverprocess.class);
		}catch(Exception e){
			throw new Exception("not="+Theserverprocess.class.getSimpleName());
		}
		Selector acceptSelector = SelectorProvider.provider().openSelector();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
		ssc.socket().bind(isa);
		ssc.register(acceptSelector,
		                SelectionKey.OP_ACCEPT);
		System.out.println(new Date()+ " "+ c.getName()+" listening port ["+port+"]");
		while (acceptSelector.select() > 0) {
			Set<SelectionKey> readyKeys = acceptSelector.selectedKeys();
			Iterator<SelectionKey> i = readyKeys.iterator();
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey)i.next();
				i.remove();
				ServerSocketChannel nextReady =(ServerSocketChannel)sk.channel();
				final Socket s = nextReady.accept().socket();
				new Thread(new Runnable(){
					public void run() {
						try{
							Theserverprocess ti = (Theserverprocess) c.getDeclaredConstructor().newInstance();
							ByteBuffer readbb = ByteBuffer.allocate(11);
							s.getChannel().read(readbb);
							Integer length = Integer.parseInt(new String(readbb.array()));
							
							if (length>0) {
								readbb.clear();
								readbb = ByteBuffer.allocate(length);
								s.getChannel().read(readbb);
								
								ti.start(readbb.array());
								
								readbb.clear();
								readbb = ByteBuffer.allocate(11);
								s.getChannel().read(readbb);
								length = Integer.parseInt(new String(readbb.array()));
								while(length>0){
									readbb.clear();
									readbb = ByteBuffer.allocate(length);
									s.getChannel().read(readbb);
									ti.process(readbb.array());
									
									readbb.clear();
									readbb = ByteBuffer.allocate(11);
									s.getChannel().read(readbb);
									length = Integer.parseInt(new String(readbb.array()));
								}
							}
							
							byte[] res = ti.end();
							if (res==null) {
								res = new byte[0];
							}
							ByteBuffer writebb = ByteBuffer.allocate(11+res.length);
							writebb.put(String.format ("%011d", res.length).getBytes());
							writebb.put(res);
							writebb.flip();
							s.getChannel().write(writebb);
						}catch(Exception e){
							StringBuffer sb = new StringBuffer();
							for (StackTraceElement ste: e.getStackTrace()){
								sb.append(ste.toString()+System.lineSeparator());
							}
							try{
								byte[] res = sb.toString().getBytes("UTF-8");
								ByteBuffer writebb = ByteBuffer.allocate(11+res.length);
								writebb.put(("-"+String.format ("%010d", res.length)).getBytes());
								writebb.put(res);
								writebb.flip();
								s.getChannel().write(writebb);
							}catch(Exception ex){
								System.out.println(new Date()+ c.getName()+" process exception:");
								e.printStackTrace();
							}
						}finally{
							if (s!=null){
								try {
									s.close();
								} catch (Exception e) {
									//do nothing
								}
							}
						}
					}
					
				}).start();
			}
		}
	}
	
}
