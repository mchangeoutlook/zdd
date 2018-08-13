package com.zdd.bdc.ex;

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
import java.util.Iterator;
import java.util.Set;

public class Theserver {

	public static void startblocking(String ip, int port, String ispending, StringBuffer pending, int bigfilehash,
			final Class<?> c) throws Exception {
		Theserverprocess test = null;
		try {
			test = (Theserverprocess) c.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new Exception("not=" + Theserverprocess.class.getSimpleName());
		}
		Selector acceptSelector = SelectorProvider.provider().openSelector();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(ip), port);
		ssc.socket().bind(isa);
		ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

		System.out.println(new Date() + " ==== " + test.getClass().getName() + " listening port [" + port + "] on ip ["
				+ ip + "] and bigfilehash = ["+bigfilehash+"]");
		while (acceptSelector.select() > 0 && !ispending.equals(pending.toString())) {
			Set<SelectionKey> readyKeys = acceptSelector.selectedKeys();
			Iterator<SelectionKey> i = readyKeys.iterator();
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey) i.next();
				i.remove();
				ServerSocketChannel nextReady = (ServerSocketChannel) sk.channel();
				final Socket s = nextReady.accept().socket();
				new Thread(new Runnable() {
					public void run() {
						InputStream is = null;
						try {
							is = s.getChannel().socket().getInputStream();
							Theserverprocess ti = (Theserverprocess) c.getDeclaredConstructor().newInstance();
							ti.init(bigfilehash);
							byte[] readbb = new byte[11];
							is.readNBytes(readbb, 0, readbb.length);
							Integer length = Integer.parseInt(new String(readbb));
							readbb = new byte[Math.abs(length)];
							is.readNBytes(readbb, 0, readbb.length);
							ti.request(readbb);

							readbb = new byte[11];
							is.readNBytes(readbb, 0, readbb.length);
							length = Integer.parseInt(new String(readbb));
							while (length > 0) {
								readbb = new byte[length];
								is.readNBytes(readbb, 0, readbb.length);
								ti.requests(readbb);
								readbb = new byte[11];
								is.readNBytes(readbb, 0, readbb.length);
								length = Integer.parseInt(new String(readbb));
							}

							byte[] res = ti.response();
							ByteBuffer writebb = ByteBuffer.allocate(11 + res.length);
							writebb.put(String.format("%011d", res.length).getBytes());
							writebb.put(res);
							writebb.flip();
							s.getChannel().write(writebb);

							InputStream responses = ti.responses();
							if (responses != null) {
								try {
									byte[] readb = new byte[10240];
									int bs = 0;
									while ((bs = responses.read(readb)) != -1) {
										writebb.clear();
										writebb = ByteBuffer.allocate(11 + bs);
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

							writebb.clear();
							writebb = ByteBuffer.allocate(11);
							writebb.put(String.format("%011d", 0).getBytes());
							writebb.flip();
							s.getChannel().write(writebb);
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

				}).start();
			}
		}
	}
}
