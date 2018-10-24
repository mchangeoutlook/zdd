package com.zdd.bdc.util;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Vector;

//file content format: value1maxlength(10)value1(value1000)value1length(11)value1maxlength(10)value2maxlength(10)value2(value2000)value2length(11)value2maxlength(10)

public class Fileutil {
	
	private static void formatdatahalf(ByteBuffer bb, byte[] value, int valuemaxlength, boolean isvaluedeleted) {
		bb.put(String.format("%010d", valuemaxlength).getBytes());
		bb.put(value);
		bb.put(new byte[valuemaxlength-value.length]);
		if (isvaluedeleted) {
			bb.put(("-"+String.format("%010d", value.length)).getBytes());
		} else {
			bb.put(String.format("%011d", value.length).getBytes());
		}
		bb.put(String.format("%010d", valuemaxlength).getBytes());
	}
	
	private static ByteBuffer formatdatapair(byte[] value1, int value1maxlength, boolean isvalue1deleted, byte[] value2, int value2maxlength, boolean isvalue2deleted) {
		ByteBuffer bb = ByteBuffer.allocate(10+value1maxlength+11+10+10+value2maxlength+11+10);
		formatdatahalf(bb, value1, value1maxlength, isvalue1deleted);
		formatdatahalf(bb, value2, value2maxlength, isvalue2deleted);
		return bb;
	}
	
	//return Vector[valuemaxlength,value,valuelength]
	private static Vector<byte[]> parsedatahalf(SeekableByteChannel sbc, boolean reverse) throws Exception{
		Vector<byte[]> returnvalue = new Vector<byte[]>(3);
		byte[] valuemaxlength = null;
		byte[] value = null;
		byte[] valuelength = null;
		if (reverse) {
			sbc.position(sbc.position()-10);
			ByteBuffer bb = ByteBuffer.allocate(10);
			sbc.read(bb);
			valuemaxlength = bb.array();
			
			int valuemaxl = Integer.parseInt(new String(valuemaxlength));
			sbc.position(sbc.position()-10-11);
			bb = ByteBuffer.allocate(11);
			sbc.read(bb);
			valuelength = bb.array();
			
			int valuel = Math.abs(Integer.parseInt(new String(valuelength)));
			sbc.position(sbc.position()-11-valuemaxl);
			bb = ByteBuffer.allocate(valuel);
			sbc.read(bb);
			value = bb.array();
			
			sbc.position(sbc.position()-valuel-10);
		} else {
			sbc.position(sbc.position());
			ByteBuffer bb = ByteBuffer.allocate(10);
			sbc.read(bb);
			valuemaxlength = bb.array();
			int valuemaxl = Integer.parseInt(new String(valuemaxlength));
			sbc.position(sbc.position()+valuemaxl);
			bb = ByteBuffer.allocate(11);
			sbc.read(bb);
			valuelength = bb.array();
			
			int valuel = Math.abs(Integer.parseInt(new String(valuelength)));
			sbc.position(sbc.position()-11-valuemaxl);
			bb = ByteBuffer.allocate(valuel);
			sbc.read(bb);
			value = bb.array();
			
			sbc.position(sbc.position()-valuel+valuemaxl+11+10);
		}
		returnvalue.add(valuemaxlength);
		returnvalue.add(value);
		returnvalue.add(valuelength);
		
		return returnvalue;
	}
	
	//return Vector[value1maxlength,value1,value1length,value2maxlength,value2,value2length]
	private static Vector<byte[]> parsedatapair(SeekableByteChannel sbc, boolean reverse) throws Exception {
		if (reverse&&sbc.position()==0||!reverse&&sbc.position()==sbc.size()) {
			return null;
		} else {
			Vector<byte[]> returnvalue = new Vector<byte[]>(6);
			Vector<byte[]> firsthalf = null;
			Vector<byte[]> secondhalf = null;
			if (reverse) {
				secondhalf = parsedatahalf(sbc, reverse);
				firsthalf = parsedatahalf(sbc, reverse);
			} else {
				firsthalf = parsedatahalf(sbc, reverse);
				secondhalf = parsedatahalf(sbc, reverse);
			}
			returnvalue.addAll(firsthalf);
			returnvalue.addAll(secondhalf);
			return returnvalue;
		}
	}
	
	public static void walkdata(Path target, Filedatawalk walker, boolean reverse) throws Exception {
		if (!Files.exists(target)) {
			//do nothing
		} else {
		SeekableByteChannel sbc = null;
		try {
			sbc = Files.newByteChannel(target, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.SYNC);
			if (reverse) {
				sbc.position(Files.size(target));
			} else {
				sbc.position(0);
			}
			Vector<byte[]> datapair = parsedatapair(sbc, reverse);
			while(datapair!=null) {
				byte[] value1maxlength = datapair.get(0);
				byte[] value1 = datapair.get(1);
				byte[] value1length = datapair.get(2);
				boolean isvalue1deleted = Integer.parseInt(new String(value1length))<0;
				
				byte[] value2maxlength = datapair.get(3);
				byte[] value2 = datapair.get(4);
				byte[] value2length = datapair.get(5);
				boolean isvalue2deleted = Integer.parseInt(new String(value2length))<0;
				
				Filedatawalkresult res = walker.data(value1, isvalue1deleted, value2, isvalue2deleted);
				if (res==null) {//move to next data
					datapair = parsedatapair(sbc, reverse);
				} else {
					if (res.getdataction()==Filedatawalkresult.DATA_DELETE) {
						ByteBuffer towrite = formatdatapair(value1, Integer.parseInt(new String(value1maxlength)), true, value2, Integer.parseInt(new String(value2maxlength)), true);
						if (reverse) {
							//keep current position;
						} else {
							sbc.position(sbc.position()-towrite.capacity());
						}
						synchronized(synckey(new String(value1))) {
							write(sbc, towrite);
						}
						if (reverse) {
							sbc.position(sbc.position()-towrite.capacity());
						} else {
							//keep current position;
						}
					} else if (res.getdataction()==Filedatawalkresult.DATA_REPLACE) {
						ByteBuffer towrite = formatdatapair(res.getvalue1(), Integer.parseInt(new String(value1maxlength)), false, res.getvalue2(), Integer.parseInt(new String(value2maxlength)), false);
						if (reverse) {
							//keep current position;
						} else {
							sbc.position(sbc.position()-towrite.capacity());
						}
						String sync = null;
						if (Arrays.equals(res.getvalue1(), value1)) {
							sync = new String(value1);
						} else {
							sync = new String(value2);
						}
						synchronized(synckey(sync)) {
							write(sbc, towrite);
						}
						if (reverse) {
							sbc.position(sbc.position()-towrite.capacity());
						} else {
							//keep current position;
						}
					} else {
						//do nothing to data
					}
					
					if (res.getwalkaction()==Filedatawalkresult.WALK_CONTINUE) {
						datapair = parsedatapair(sbc, reverse);
					} else {
						break;
					}
				}
			}
			
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
		}
	}
	
	private static void write(Path target, long startposition, ByteBuffer towrite) throws Exception {
		if (!Files.exists(target) && target.getParent() != null && !Files.exists(target.getParent())) {
			Files.createDirectories(target.getParent());
		}
		SeekableByteChannel sbc = null;
		try {
			sbc = Files.newByteChannel(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
					StandardOpenOption.SYNC);
			sbc.position(startposition);
			write(sbc, towrite);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}

	private static void write(SeekableByteChannel sbc, ByteBuffer towrite) throws Exception {
		towrite.flip();
		sbc.write(towrite);
	}

	public static void create(byte[] value1, int value1maxlength, byte[] value2, int value2maxlength, Path target) throws Exception {
		ByteBuffer towrite = formatdatapair(value1, value1maxlength, false, value2, value2maxlength, false);
		synchronized(syncfile(target)) {
			if (Files.exists(target)) {
				write(target, Files.size(target), towrite);
			} else {
				write(target, 0, towrite);
			}
		}
	}
	
	public static void modifyfirstvalue2byvalue1(byte[] value1, byte[] newvalue2, Path target) throws Exception {
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_REPLACE, value1, newvalue2);
					} else {
						//ignore the data
						return null;
					}
				}
			}
			
		}, false);
	}

	public static void modifylastvalue2byvalue1(byte[] value1, byte[] newvalue2, Path target) throws Exception {
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_REPLACE, value1, newvalue2);
					} else {
						//ignore the data
						return null;
					}
				}
			}
			
		}, true);
	}

	public static void deletelastvalue2byvalue1(byte[] value1, Path target) throws Exception {
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DELETE, null, null);
					} else {
						//ignore the data
						return null;
					}
				}
			}
			
		}, true);
	}
	
	public static Vector<byte[]> readfirstvalue1value2(Path target) throws Exception {
		Vector<byte[]> returnvalue = new Vector<byte[]>(2);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				returnvalue.add(v1);
				returnvalue.add(v2);
				return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING, null, null);
			}
			
		}, false);
		return returnvalue;
	}

	public static byte[] readfirstvalue2byvalue1(byte[] value1, Path target) throws Exception {
		Vector<byte[]> returnvalue = new Vector<byte[]>(1);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						returnvalue.add(v2);
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING, null, null);
					} else {
						//ignore the data
						return null;
					}
				}
			}
			
		}, false);
		if (returnvalue.isEmpty()) {
			return null;
		} else {
			return returnvalue.get(0);
		}
	}
	
	public static byte[] readlastvalue2byvalue1(byte[] value1, Path target) throws Exception {
		Vector<byte[]> returnvalue = new Vector<byte[]>(1);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						returnvalue.add(v2);
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING, null, null);
					} else {
						//ignore the data
						return null;
					}
				}
			}
			
		}, true);
		if (returnvalue.isEmpty()) {
			return null;
		} else {
			return returnvalue.get(0);
		}

	}
	
	public static Vector<byte[]> readallvalue2byvalue1(byte[] value1, int defaultnumofvalues, boolean reverse, Path target) throws Exception {
		Vector<byte[]> returnvalue = new Vector<byte[]>(defaultnumofvalues);
		walkdata(target, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(byte[] v1, boolean isv1deleted, byte[] v2,
					boolean isv2deleted) {
				if (isv1deleted||isv2deleted) {
					return null;
				} else {
					if (Arrays.equals(value1, v1)) {
						returnvalue.add(v2);
					} else {
						//ignore the data
					}
					return null;
				}
			}
			
		}, reverse);
		return returnvalue;
	}
	
	//to avoid dead lock, sync file and key on different range.
	private static final int synchash = 10000;
	public static String syncfile(Path tosync) {
		
		return String.valueOf(Math.abs(tosync.getFileName().toString().hashCode()%synchash)+synchash).intern();
	}
	
	public static String synckey(String tosync) {
		return String.valueOf(Math.abs(tosync.hashCode()%synchash)).intern();
	}
	
}
