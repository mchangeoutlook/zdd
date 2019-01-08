package com.zdd.bdc.server.util;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

//value1 is key, value2 is value
//file content lines format: key#valuemaxlength(11,the length of max integer with one signal minus at front which means deleted)#value

public class Filekvutil {
	
	private static final String plitter = "#";
	private static final String charset = "UTF-8";

	private static String[] split(String line) {
		try {
			String[] returnvalue = line.split(plitter);
			for (int i = 0; i < returnvalue.length; i++) {
				returnvalue[i] = URLDecoder.decode(returnvalue[i].trim(), charset);
			}
			return returnvalue;
		} catch (Exception e) {
			return null;
		}
	}

	private static String split(String key, int extravaluecapacity, String value) {
		try {
			if (value == null) {
				value = "";
			}
			int valuemaxlength = encbytes(value).length + extravaluecapacity;
			return URLEncoder.encode(key, charset) + plitter + String.format("%011d", valuemaxlength) + plitter
					+ URLEncoder.encode(value, charset);
		} catch (Exception e) {
			return null;
		}
	}

	private static byte[] encbytes(String str) {
		try {
			return URLEncoder.encode(str,charset).getBytes(charset);
		} catch (Exception e) {
			return null;
		}
	}

	private static byte[] bytes(String str) {
		try {
			return str.getBytes(charset);
		} catch (Exception e) {
			return null;
		}
	}

	private static void write(Path target, long startposition, ByteBuffer towrite) throws Exception {
		SeekableByteChannel sbc = null;
		try {
			sbc = Files.newByteChannel(target, StandardOpenOption.WRITE, StandardOpenOption.SYNC);
			sbc.position(startposition);
			towrite.flip();
			sbc.write(towrite);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}

	public static void walkdata(Path target, Filedatawalk walker) throws Exception {
		if (!Files.exists(target)) {
			// do nothing
		} else {
			BufferedReader br = null;
			try {
				br = Fileutil.br(target);

				String line = br.readLine();
				long datasequence = 0;
				long dataseqincludedeleted = 0;
				long position = 0;
				while (line != null) {
					String[] parts = split(line);
					String key = parts[0];
					boolean isvaluedeleted = parts[1].startsWith("-");
					String value = parts.length == 3 ? parts[2] : "";

					Filedatawalkresult res = walker.data(datasequence, dataseqincludedeleted, key, value,
							isvaluedeleted);
					if (res == null) {// move to next data
						if (line != null) {
							position += encbytes(key).length + 13 + Math.abs(Integer.parseInt(parts[1])) + 1;
						}
						line = br.readLine();
						dataseqincludedeleted++;
						if (!isvaluedeleted) {
							datasequence++;
						}
					} else {
						if (res.getdataction() == Filedatawalkresult.DATA_DELETE) {
							ByteBuffer towrite = ByteBuffer.allocate(1);
							towrite.put(bytes("-"));
							synchronized (Fileutil.synckey(key)) {
								write(target, position + encbytes(key).length + 1, towrite);
							}
						} else if (res.getdataction() == Filedatawalkresult.DATA_REPLACE) {
							int valuemaxlength = Math.abs(Integer.parseInt(parts[1]));
							ByteBuffer towrite = ByteBuffer.allocate(valuemaxlength);
							byte[] newval = encbytes(res.getnewvalue());
							try {
								towrite.put(newval);
							}catch(BufferOverflowException e) {
								throw new Exception("exceed"+valuemaxlength);
							}
							towrite.put(new byte[valuemaxlength - newval.length]);
							synchronized (Fileutil.synckey(key)) {
								write(target, position + encbytes(key).length + 13, towrite);
							}
						} else {
							// do nothing to data
						}

						if (res.getwalkaction() == Filedatawalkresult.WALK_CONTINUE) {
							if (line != null) {
								position += encbytes(key).length + 13 + Math.abs(Integer.parseInt(parts[1])) + 1;
							}
							line = br.readLine();
							dataseqincludedeleted++;
							if (!isvaluedeleted) {
								datasequence++;
							}
						} else {
							break;
						}
					}
				}

			} finally {
				if (br != null) {
					br.close();
				}
			}
		}
	}

	public static void create(String key, String value, int extravaluecapacity, Path target) throws Exception {
		String tocreate = split(key, extravaluecapacity, value);
		byte[] tocreateb = bytes(tocreate);
		ByteBuffer towrite = ByteBuffer.allocate(tocreateb.length + extravaluecapacity + 1);
		towrite.put(tocreateb);
		towrite.put(new byte[extravaluecapacity]);
		towrite.put(bytes(System.lineSeparator()));
		synchronized (Fileutil.syncfile(target)) {
			if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
				Files.createDirectories(target.getParent());
			}
			Files.write(target, towrite.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND,
					StandardOpenOption.SYNC);
		}
	}

	public static void modifyvaluebykey(String thekey, String thevalue, Path target) throws Exception {
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_REPLACE, thevalue);
					} else {
						// ignore the data
						return null;
					}
				}
			}

		});
	}

	public static void deletevaluebykey(String thekey, Path target) throws Exception {
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DELETE,
									null);
					} else {
						return null;
					}
				}
			}

		});
	}

	public static Vector<String> readfirstkeyvalue(Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(2);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvalluedeleted) {
				returnvalue.add(key);
				returnvalue.add(value);
				return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING,
						null);
			}

		});
		return returnvalue;
	}

	public static String readvaluebykey(String thekey, Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(1);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						returnvalue.add(value);
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null);
					} else {
						// ignore the data
						return null;
					}
				}
			}

		});
		if (returnvalue.isEmpty()) {
			return null;
		} else {
			return returnvalue.get(0);
		}
	}

	public static Vector<String> readallvaluesbykey(String thekey, Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(500);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						returnvalue.add(value);
					} else {
						// ignore the data
					}
					return null;
				}
			}

		});
		return returnvalue;
	}

}
