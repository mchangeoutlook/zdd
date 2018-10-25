package com.zdd.bdc.client.ex;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Theclient {

	public static byte[] request(String ip, int port, byte[] request, InputStream requests, Theclientprocess cp)
			throws Exception {
		SocketChannel sc = null;
		ByteBuffer writebb = null;
		try {
			sc = SocketChannel.open();

			sc.connect(new InetSocketAddress(InetAddress.getByName(ip), port));
			int bs = request.length;
			writebb = ByteBuffer.allocate(11 + bs);
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
		} catch(Exception ex) {
			try {
				sc.close();
			} catch (Exception e) {
				// do nothing
			}
			throw ex;
		}
		
		InputStream is = null;
		try {
			is = sc.socket().getInputStream();
			byte[] readbb = new byte[11];
			is.readNBytes(readbb, 0, readbb.length);
			Integer length = Integer.parseInt(new String(readbb));
			readbb = new byte[Math.abs(length)];
			is.readNBytes(readbb, 0, readbb.length);
			byte[] returnvalue = readbb;
			if (length < 0) {
				throw new Exception(new String(returnvalue, "UTF-8"));
			}
			readbb = new byte[11];
			is.readNBytes(readbb, 0, readbb.length);
			length = Integer.parseInt(new String(readbb));
			while (length > 0) {
				readbb = new byte[length];
				is.readNBytes(readbb, 0, readbb.length);
				cp.responses(readbb);

				readbb = new byte[11];
				is.readNBytes(readbb, 0, readbb.length);
				length = Integer.parseInt(new String(readbb));
			}
			if (length < 0) {
				readbb = new byte[Math.abs(length)];
				is.readNBytes(readbb, 0, readbb.length);
				throw new Exception(new String(readbb, "UTF-8"));
			}
			
			writebb.clear();
			writebb = ByteBuffer.allocate(11);
			writebb.put(String.format("%011d", 0).getBytes());
			writebb.flip();
			sc.write(writebb);
			return returnvalue;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// do nothing
			}
			try {
				sc.close();
			} catch (Exception e) {
				// do nothing
			}

		}
	}
}