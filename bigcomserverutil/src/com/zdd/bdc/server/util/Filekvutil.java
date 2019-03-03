package com.zdd.bdc.server.util;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Vector;

/*
 *The key is a specified length of chars.
 *The format of each line in index/data file: key(specified bytes)valuemaxlength(11bytes, if deleted, the first char is "-")value(valuemaxlength bytes)LineSeparator		
 *for paged index file, key is key, value is index
 *for data file, key is key, value is value.
 */
public class Filekvutil {

	private static final int valuemaxlengthbytes = 11;
	private static final String charset = "UTF-8";

	private static String[] line_to_key_valuemaxlength_value(int specifiedkeylength, String line) throws Exception {
		String[] returnvalue = new String[3];
		returnvalue[0] = line.substring(0, specifiedkeylength);
		returnvalue[1] = line.substring(specifiedkeylength, specifiedkeylength + valuemaxlengthbytes);
		returnvalue[2] = line.substring(specifiedkeylength + valuemaxlengthbytes).trim();
		return returnvalue;
	}

	private static String key_value_to_line(int specifiedkeylength, String key, int extravaluecapacity, String value)
			throws Exception {
		if (bytes(key).length != specifiedkeylength) {
			throw new Exception("keylengthnot" + specifiedkeylength);
		}
		if (value == null) {
			value = "";
		}
		int valuemaxlength = bytes(value).length + extravaluecapacity;
		if (extravaluecapacity > 0) {
			return key + String.format("%0" + valuemaxlengthbytes + "d", valuemaxlength) + value
					+ new String(new byte[extravaluecapacity]);
		} else {
			return key + String.format("%0" + valuemaxlengthbytes + "d", valuemaxlength) + value;
		}
	}

	private static byte[] bytes(String str) throws Exception {
		return str.getBytes(charset);
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

	public static void walkdata(int specifiedkeylength, Path target, Filedatawalk walker) throws Exception {
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
					String[] parts = line_to_key_valuemaxlength_value(specifiedkeylength, line);
					String key = parts[0];
					boolean isvaluedeleted = parts[1].startsWith("-");
					String value = parts[2];

					Filedatawalkresult res = walker.data(datasequence, dataseqincludedeleted, key, value,
							isvaluedeleted);
					if (res == null) {// move to next data
						if (line != null) {
							position += specifiedkeylength + valuemaxlengthbytes + Math.abs(Integer.parseInt(parts[1]))
									+ 1;
						}
						line = br.readLine();
						dataseqincludedeleted++;
						if (!isvaluedeleted) {
							datasequence++;
						}
					} else {
						if (res.getdataction() == Filedatawalkresult.DATA_DELETE) {
							ByteBuffer towrite4del = ByteBuffer.allocate(1);
							towrite4del.put(bytes("-"));
							synchronized (Fileutil.synckey(key)) {
								write(target, position + specifiedkeylength, towrite4del);
							}
						} else if (res.getdataction() == Filedatawalkresult.DATA_REPLACE_VALUE) {
							int valuemaxlength = Math.abs(Integer.parseInt(parts[1]));
							byte[] newval = bytes(res.getnewvalue());
							if (newval.length > valuemaxlength) {
								throw new Exception("exceed" + valuemaxlength);
							}
							byte[] oldval = bytes(value);
							ByteBuffer towritevalue = null;
							if (oldval.length > newval.length) {
								towritevalue = ByteBuffer.allocate(oldval.length);
								towritevalue.put(newval);
								towritevalue.put(new byte[oldval.length - newval.length]);
							} else {
								towritevalue = ByteBuffer.allocate(newval.length);
								towritevalue.put(newval);
							}
							synchronized (Fileutil.synckey(key)) {
								write(target, position + specifiedkeylength + valuemaxlengthbytes, towritevalue);
							}
						} else if (res.getdataction() == Filedatawalkresult.DATA_REPLACE_KEY) {
							byte[] newkey = bytes(res.getnewkey());
							if (newkey.length != specifiedkeylength) {
								throw new Exception("keylengthnot" + specifiedkeylength);
							}
							ByteBuffer towritekey = ByteBuffer.allocate(specifiedkeylength);
							towritekey.put(newkey);
							synchronized (Fileutil.synckey(value)) {
								write(target, position, towritekey);
							}
						} else {
							// do nothing to data
						}

						if (res.getwalkaction() == Filedatawalkresult.WALK_CONTINUE) {
							if (line != null) {
								position += specifiedkeylength + valuemaxlengthbytes
										+ Math.abs(Integer.parseInt(parts[1])) + 1;
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

	public static void create(int specifiedkeylength, String key, String value, int extravaluecapacity, Path target)
			throws Exception {
		String tocreate = key_value_to_line(specifiedkeylength, key, extravaluecapacity, value);
		synchronized (Fileutil.syncfile(target)) {
			if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
				Files.createDirectories(target.getParent());
			}
			Files.write(target, bytes(tocreate + System.lineSeparator()), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND, StandardOpenOption.SYNC);
		}
	}

	public static void modifyvaluebykey(int specifiedkeylength, String thekey, String thevalue, Path target)
			throws Exception {
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_REPLACE_VALUE, null, thevalue);
					} else {
						// ignore the data
						return null;
					}
				}
			}

		});
	}
	
	public static void modifykeybyvalue(int specifiedkeylength, String thekey, String thevalue, Path target)
			throws Exception {
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (value.equals(thevalue)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_REPLACE_KEY, thekey, null);
					} else {
						// ignore the data
						return null;
					}
				}
			}

		});
	}

	public static void deletevaluebykey(int specifiedkeylength, String thekey, Path target) throws Exception {
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DELETE,
								null, null);
					} else {
						return null;
					}
				}
			}

		});
	}

	public static Vector<String> readfirstkeyvalue(int specifiedkeylength, Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(2);
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvalluedeleted) {
				returnvalue.add(key);
				returnvalue.add(value);
				return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING,
						null, null);
			}

		});
		return returnvalue;
	}

	public static String readvaluebykey(int specifiedkeylength, String thekey, Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(1);
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (key.equals(thekey)) {
						returnvalue.add(value);
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null, null);
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
	
	public static String readkeybyvalue(int specifiedkeylength, String thevalue, Path target) throws Exception {
		Vector<String> returnvalue = new Vector<String>(1);
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (value.equals(thevalue)) {
						returnvalue.add(key);
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null, null);
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

	public static Vector<String> readallkeysbyvalue(int specifiedkeylength, String thevalue, Path target)
			throws Exception {
		Vector<String> returnvalue = new Vector<String>(500);
		walkdata(specifiedkeylength, target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, String key, String value,
					boolean isvaluedeleted) {
				if (isvaluedeleted) {
					return null;
				} else {
					if (value.equals(thevalue)) {
						returnvalue.add(key);
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
