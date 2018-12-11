package com.zdd.bdc.server.ex;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Theserver {

	private static final Map<String, Theserverprocess> serverprocesses = new Hashtable<String, Theserverprocess>(); 
	
	public synchronized static void startblocking(ExecutorService es, String ip, int port, String ispending, StringBuffer pending, int bigfilehash,
			final Class<?> c, Map<String, Object> additionalserverconfig) throws Exception {
		try {
			Theserverprocess ti = (Theserverprocess) c.getDeclaredConstructor().newInstance();
			serverprocesses.put(c.getName(), ti);
		} catch (Exception e) {
			throw new Exception(new Date()+" ==== "+c.getName()+" !instanceof " + Theserverprocess.class.getSimpleName());
		}
		try {
			serverprocesses.get(c.getName()).init(ip, port, bigfilehash, additionalserverconfig);
		} catch (Exception e) {
			System.out.println(new Date()+" ==== error init "+c.getName());
			throw e;
		}
		Selector acceptSelector = SelectorProvider.provider().openSelector();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(ip), port);
		ssc.socket().bind(isa);
		ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

		System.out.println(new Date() + " ==== " + c.getName() + " listening port [" + port + "] on ip ["
				+ ip + "] and bigfilehash = ["+bigfilehash+"]");
		while (acceptSelector.select() > 0 && !ispending.equals(pending.toString())) {
			Set<SelectionKey> readyKeys = acceptSelector.selectedKeys();
			Iterator<SelectionKey> i = readyKeys.iterator();
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey) i.next();
				i.remove();
				ServerSocketChannel nextReady = (ServerSocketChannel) sk.channel();
				final Socket s = nextReady.accept().socket();
				es.execute(new Runnable() {
					public void run() {
						InputStream is = null;
						try {
							is = s.getChannel().socket().getInputStream();
							byte[] readbb = new byte[11];
							is.readNBytes(readbb, 0, readbb.length);
							Integer length = Integer.parseInt(new String(readbb));
							byte[] parambb = new byte[Math.abs(length)];
							is.readNBytes(parambb, 0, parambb.length);
							
							Theserverprocess ti = serverprocesses.get(c.getName());
							
							byte[] res = ti.request(parambb);
							
							if (res==null) {
								ByteBuffer writebb = ByteBuffer.allocate(11);
								writebb.put(String.format("%011d", -1).getBytes());
								writebb.flip();
								s.getChannel().write(writebb);
							} else {
								ByteBuffer writebb = ByteBuffer.allocate(11 + res.length);
								writebb.put(String.format("%011d", res.length).getBytes());
								writebb.put(res);
								writebb.flip();
								s.getChannel().write(writebb);
							}
							
							readbb = new byte[11];
							is.readNBytes(readbb, 0, readbb.length);
							length = Integer.parseInt(new String(readbb));
							Inputprocess inp = null;
							while (length > 0) {
								if (inp==null) {
									inp = ti.requestinput(parambb);
								}
								readbb = new byte[length];
								is.readNBytes(readbb, 0, readbb.length);
								inp.process(readbb);
								readbb = new byte[11];
								is.readNBytes(readbb, 0, readbb.length);
								length = Integer.parseInt(new String(readbb));
							}
							
							InputStream responses = ti.requestoutput(parambb);
							if (responses != null) {
								try {
									byte[] readb = new byte[10240];
									int bs = 0;
									while ((bs = responses.read(readb)) != -1) {
										ByteBuffer writebb = ByteBuffer.allocate(11 + bs);
										writebb.put(String.format("%011d", bs).getBytes());
										if (bs != readb.length) {
											readb = Arrays.copyOf(readb, bs);
										}
										writebb.put(readb);
										writebb.flip();
										s.getChannel().write(writebb);

									}
								} finally {
									responses.close();
								}
							}

							ByteBuffer writebb = ByteBuffer.allocate(11);
							writebb.put(String.format("%011d", 0).getBytes());
							writebb.flip();
							s.getChannel().write(writebb);
							try {
								readbb = new byte[11];
								is.readNBytes(readbb, 0, readbb.length);
								length = Integer.parseInt(new String(readbb));
								if (length==0) {
									try {
										is.close();
									} catch (Exception e) {
										// do nothing
									}

									try {
										s.close();
									} catch (Exception e) {
										// do nothing
									}
								}
							}catch(Exception ex) {
								//do nothing
							}
						} catch (Exception e) {
							try {
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								byte[] res = errors.toString().getBytes("UTF-8");
								ByteBuffer writebb = ByteBuffer.allocate(11 + res.length);
								writebb.put((String.format("%011d", -1 * res.length)).getBytes());
								writebb.put(res);
								writebb.flip();
								s.getChannel().write(writebb);
							} catch (Exception ex) {
								System.out.println(new Date() + " ==== " + c.getName() + " process exception:");
								ex.printStackTrace();
								System.out.println(new Date() + " ==== " + c.getName() + " due to exception:");
								e.printStackTrace();
							}
						} finally {
							try {
								is.close();
							} catch (Exception e) {
								// do nothing
							}

							try {
								s.close();
							} catch (Exception e) {
								// do nothing
							}
						}
					}

				});
			}
		}
		es.shutdownNow();
	}
}
