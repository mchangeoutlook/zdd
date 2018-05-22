package com.zdd.bdc.util;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Bigdatafileutil {

	public static byte[] read(String key, Path target) throws Exception {
		byte[] returnvalue = null;
		long position = Files.exists(target) ? Files.size(target) : 0;
		if (position != 0) {
			SeekableByteChannel sbc = null;
			try {
				sbc = Files.newByteChannel(target, StandardOpenOption.READ);
				position = findkey(sbc, position, key);
				if (position != -1) {
					position -= 11;
					sbc.position(position);
					ByteBuffer bbmax = ByteBuffer.allocate(11);
					sbc.read(bbmax);
					int max = Integer.parseInt(new String(bbmax.array()));

					position -= 10;
					sbc.position(position);
					ByteBuffer bbvaluelength = ByteBuffer.allocate(10);
					sbc.read(bbvaluelength);
					int valuelength = Integer.parseInt(new String(bbvaluelength.array()));

					ByteBuffer bbvalue = ByteBuffer.allocate(valuelength);
					sbc.position(position - max);
					sbc.read(bbvalue);
					returnvalue = bbvalue.array();
				}
			} finally {
				if (sbc != null) {
					sbc.close();
				}
			}
		}
		return returnvalue;
	}

	public static synchronized long increment(String key, Path target, long amount) throws Exception {
		long returnvalue = 0l;
		SeekableByteChannel sbc = null;
		byte[] bkey = key.getBytes("UTF-8");
		if (bkey.length != 40) {
			throw new Exception("key40");
		}
		
		try {
			if (!Files.exists(target)) {
				write(bkey, target, -1, String.valueOf(amount).getBytes(), 20);
				returnvalue = amount;
			} else {
				long position = Files.exists(target) ? Files.size(target) : 0;
				if (position == 0) {
					write(bkey, target, -1, String.valueOf(amount).getBytes(), 20);
					returnvalue = amount;
				} else {
					sbc = Files.newByteChannel(target, StandardOpenOption.READ);
					position = findkey(sbc, position, key);
					if (position == -1) {
						write(bkey, target, -1, String.valueOf(amount).getBytes(), 20);
						returnvalue = amount;
					} else {
						position -= (11+10);
						sbc.position(position);
						ByteBuffer bbvaluelength = ByteBuffer.allocate(10);
						sbc.read(bbvaluelength);
						int valuelength = Integer.parseInt(new String(bbvaluelength.array()));

						ByteBuffer bbvalue = ByteBuffer.allocate(valuelength);
						sbc.position(position - 20);
						sbc.read(bbvalue);
						try {
							long current = Long.parseLong(new String(bbvalue.array()));
							if (amount==0) {
								returnvalue = current;
							} else {
								long newvalue = current + amount;
								write(String.valueOf(newvalue).getBytes(), target, position - 20, 20);
								returnvalue = newvalue;
							}
						}catch(NumberFormatException e) {
							throw new Exception("notLong");
						}
					}
				}
				
			}
		}finally {
			if (sbc != null) {
				sbc.close();
			}
		}

		return returnvalue;
	}

	public static void delete(String key, Path target) throws Exception {
		SeekableByteChannel sbc = null;
		try {
			long position = Files.exists(target) ? Files.size(target) : 0;
			if (position == 0) {
				throw new Exception("empty");
			}
			sbc = Files.newByteChannel(target, StandardOpenOption.READ);
			position = findkey(sbc, position, key);
			if (position == -1) {
				throw new Exception("notfound");
			}
			long startposition = position - 11;
			ByteBuffer bb = ByteBuffer.allocate(1);
			bb.put("-".getBytes());
			write(target, startposition, bb);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}

	}

	public static synchronized void create(String key, Path target, byte[] value, int max) throws Exception {
		SeekableByteChannel sbc = null;
		byte[] bkey = key.getBytes("UTF-8");
		if (bkey.length != 40) {
			throw new Exception("key40");
		}
		if (max == 0 || value.length > max) {
			throw new Exception("exceed");
		}
		try {
			write(bkey, target, -1, value, max);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}

	public static void modify(String key, Path target, byte[] value) throws Exception {
		SeekableByteChannel sbc = null;
		try {
			long position = Files.exists(target) ? Files.size(target) : 0;
			if (position == 0) {
				throw new Exception("empty");
			}
			sbc = Files.newByteChannel(target, StandardOpenOption.READ);
			position = findkey(sbc, position, key);
			if (position == -1) {
				throw new Exception("notfound");
			}
			position -= 11;
			sbc.position(position);
			ByteBuffer bbmax = ByteBuffer.allocate(11);
			sbc.read(bbmax);
			int max = Integer.parseInt(new String(bbmax.array()));
			if (max < value.length) {
				throw new Exception("exceed");
			}
			long startposition = position - max - 10;
			write(value, target, startposition, max);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}

	private static long findkey(SeekableByteChannel sbc, long fromposition, String key) throws Exception {
		long returnvalue = -1;
		long position = fromposition - 40;
		sbc.position(position);
		ByteBuffer bbkey = ByteBuffer.allocate(40);
		sbc.read(bbkey);
		position -= 11;
		sbc.position(position);
		ByteBuffer bbmax = ByteBuffer.allocate(11);
		sbc.read(bbmax);
		int max = Integer.parseInt(new String(bbmax.array()));
		byte[] bkey = key.getBytes("UTF-8");
		while ((!Arrays.equals(bbkey.array(), bkey) || max < 0) && position != 0) {
			position -= (10 + Math.abs(max));
			if (position != 0) {
				position -= 40;
				sbc.position(position);
				bbkey = ByteBuffer.allocate(40);
				sbc.read(bbkey);
				position -= 11;
				sbc.position(position);
				bbmax = ByteBuffer.allocate(11);
				sbc.read(bbmax);
				max = Integer.parseInt(new String(bbmax.array()));
			}
		}

		if (max>=0&&Arrays.equals(bbkey.array(), bkey)) {
			returnvalue = position + 11;
		}
		return returnvalue;
	}
	
	private static void write(byte[] bkey, Path target, long startposition, byte[] value, int max) throws Exception {
		ByteBuffer bbvalue = ByteBuffer.allocate(max);
		bbvalue.put(value);
		ByteBuffer bb = ByteBuffer.allocate(max + 10 + 11 + 40);
		bb.put(bbvalue.array()).put(String.format("%010d", value.length).getBytes())
				.put(String.format("%011d", max).getBytes()).put(bkey);

		write(target, startposition, bb);
	}

	private static void write(byte[] value, Path target, long startposition, int max) throws Exception {
		ByteBuffer bbvalue = ByteBuffer.allocate(max);
		bbvalue.put(value);
		ByteBuffer bb = ByteBuffer.allocate(max + 10);
		bb.put(bbvalue.array()).put(String.format("%010d", value.length).getBytes());
		write(target, startposition, bb);
	}

	private static void write(Path target, long startposition, ByteBuffer bb) throws Exception {
		if (startposition == -1) {
			Files.write(target, bb.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND,
					StandardOpenOption.SYNC);
		} else {
			SeekableByteChannel sbc = null;
			try {
				sbc = Files.newByteChannel(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
						StandardOpenOption.SYNC);
				sbc.position(startposition);
				bb.flip();
				sbc.write(bb);
			} finally {
				if (sbc != null) {
					sbc.close();
				}
			}
		}
	}

}
