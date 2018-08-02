package com.zdd.bdc.ex;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Theclient {

	public static byte[] request(String ip, int port, byte[] request, InputStream requests, Theclientprocess cp)
			throws Exception {
		if (request == null || request.length == 0) {
			throw new Exception("norequest");
		}
		if (ip.trim().isEmpty()) {
			throw new Exception("noip");
		}
		SocketChannel sc = null;
		try {
			sc = SocketChannel.open();

			sc.connect(new InetSocketAddress(InetAddress.getByName(ip), port));
			sc.configureBlocking(true);
			int bs = request.length;
			ByteBuffer writebb = ByteBuffer.allocate(11 + bs);
			writebb.put(String.format("%011d", bs).getBytes());
			writebb.put(request);
			writebb.flip();
			sc.write(writebb);

			if (requests != null) {
				try {
					byte[] readb = new byte[10240];
					bs = 0;
					while ((bs = requests.read(readb)) != -1) {
						writebb.clear();
						writebb = ByteBuffer.allocate(11 + bs);
						writebb.put(String.format("%011d", bs).getBytes());
						if (bs != readb.length) {
							readb = Arrays.copyOf(readb, bs);
						}
						writebb.put(readb);
						writebb.flip();
						sc.write(writebb);
					}
				} finally {
					requests.close();
				}
			}

			writebb.clear();
			writebb = ByteBuffer.allocate(11);
			writebb.put(String.format("%011d", 0).getBytes());
			writebb.flip();
			sc.write(writebb);
			sc.shutdownOutput();

			ByteBuffer readbb = ByteBuffer.allocate(11);
			sc.read(readbb);
			Integer length = Integer.parseInt(new String(readbb.array()));
			readbb = ByteBuffer.allocate(Math.abs(length));
			sc.read(readbb);
			byte[] returnvalue = readbb.array();

			readbb.clear();
			readbb = ByteBuffer.allocate(11);
			sc.read(readbb);
			length = Integer.parseInt(new String(readbb.array()));
			while (length > 0) {
				readbb.clear();
				readbb = ByteBuffer.allocate(length);
				sc.read(readbb);
				cp.responses(readbb.array());

				readbb.clear();
				readbb = ByteBuffer.allocate(11);
				sc.read(readbb);
				length = Integer.parseInt(new String(readbb.array()));
			}
			
			return returnvalue;
		} finally {
			sc.shutdownInput();
			if (sc != null) {
				sc.close();
			}
		}
	}
}