package com.zdd.bdc.server.util;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;

import com.zdd.bdc.client.util.STATIC;

public class Fileuniqueutil {

	private static final Path uniquerootfolder = STATIC.LOCAL_DATAFOLDER;
	private static final int capacitykeymax = 60;
	private static final int capacityvalue = 40;
	private static final int[] rootrange = { 8, 16 };
	private static final long leafmax = Long.parseLong(
			"1" + String.format("%0" + (String.valueOf(Long.MAX_VALUE).length() - rootrange[1]) + "d", 0)) - 1;

	private static long hashkey(byte[] key) {
		return Math.abs(UUID.nameUUIDFromBytes(key).getLeastSignificantBits());
	}

	private static long hashleaf(byte[] key) {
		return Long.parseLong(String.valueOf(hashkey(key)).substring(rootrange[0], rootrange[1]));
	}

	private static long hashvalue(byte[] key, int distributions) throws Exception {
		if (distributions <= 0) {
			throw new Exception("distributionsnegative");
		}
		if (distributions > leafmax) {
			throw new Exception("distributexceed" + leafmax);
		}

		long leaf = Long.parseLong(String.valueOf(hashkey(key)).substring(rootrange[1]));
		long leafseg = leafmax / distributions;
		return leaf - leafseg * (leaf / leafseg);
	}

	private static Path uniquefolder() throws Exception {
		Path returnvalue = uniquerootfolder.resolve("#unique#");
		if (!Files.exists(returnvalue)) {
			Files.createDirectories(returnvalue);
		}
		return returnvalue;
	}

	private static Path rootfile(String filter) throws Exception {
		Path returnvalue = uniquefolder().resolve(filterfolder(filter)).resolve("root");
		if (!Files.exists(returnvalue.getParent())) {
			Files.createDirectories(returnvalue.getParent());
		}
		if (!Files.exists(returnvalue)) {
			Files.write(returnvalue, new byte[0], StandardOpenOption.CREATE);
		}
		return returnvalue;
	}

	private static String filterfolder(String filter) {
		String filterfolder = STATIC.urlencode(filter);
		if (filterfolder == null || filterfolder.trim().isEmpty()) {
			filterfolder = "defaultfilter";
		}
		return filterfolder;
	}

	private static Path leafolder(String filter) throws Exception {
		Path returnvalue = uniquefolder().resolve(filterfolder(filter)).resolve("leaf");
		if (!Files.exists(returnvalue)) {
			Files.createDirectories(returnvalue);
		}
		return returnvalue;
	}

	private static Path collisionfolder(String filter) throws Exception {
		Path returnvalue = uniquefolder().resolve(filterfolder(filter)).resolve("collision");
		if (!Files.exists(returnvalue)) {
			Files.createDirectories(returnvalue);
		}
		return returnvalue;
	}

	private static ByteBuffer formatkeyvalue(byte[] key, byte[] value) throws Exception {
		if (key == null || key.length == 0) {
			throw new Exception("nokey");
		}
		if (key.length > capacitykeymax) {
			throw new Exception("keyexceed" + capacitykeymax);
		}
		if (key[0] == 0) {
			throw new Exception("invalidkey");
		}
		if (value == null || value.length == 0) {
			throw new Exception("novalue");
		}
		if (value.length != capacityvalue) {
			throw new Exception("valuenot" + capacityvalue);
		}
		if (value[0] == 0) {
			throw new Exception("invalidvalue");
		}
		ByteBuffer returnvalue = ByteBuffer.allocate(capacityvalue + capacitykeymax);
		returnvalue.put(value);
		returnvalue.put(key);
		if (key.length < capacitykeymax) {
			returnvalue.put(new byte[capacitykeymax - key.length]);
		}
		return returnvalue;
	}

	private static void collision(Path target, byte[] key, ByteBuffer towrite, ByteBuffer toread) throws Exception {
		if (!Files.exists(target)) {
			Files.write(target, new byte[0], StandardOpenOption.CREATE);
		}
		SeekableByteChannel sbc = null;
		try {
			sbc = Files.newByteChannel(target, StandardOpenOption.WRITE, StandardOpenOption.READ,
					StandardOpenOption.SYNC);
			long position = 0;
			sbc.position(position);

			ByteBuffer read = ByteBuffer.allocate(capacityvalue + capacitykeymax);
			sbc.read(read);
			byte[] r = read.array();
			byte[] t = null;
			if (towrite != null) {
				t = towrite.array();
			}
			while (r[0] != 0) {
				if (t != null) {
					boolean duplicate = true;
					for (int i = capacityvalue; i < capacityvalue + capacitykeymax; i++) {
						if (t[i] != r[i]) {
							duplicate = false;
							break;
						}
					}
					if (duplicate) {
						throw new Exception("duplicate");
					}
				} else {
					boolean samekey = true;
					for (int i = 0; i < key.length; i++) {
						if (key[i] != r[capacityvalue + i]) {
							samekey = false;
							break;
						}
					}
					if (samekey) {
						toread.put(Arrays.copyOf(r, capacityvalue));
						break;
					}
				}
				read = ByteBuffer.allocate(capacityvalue + capacitykeymax);
				sbc.read(read);
				r = read.array();
			}
			if (t != null) {
				towrite.flip();
				sbc.write(towrite);
			}
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}

	public static void create(String filter, byte[] key, byte[] value, int distributions) throws Exception {
		ByteBuffer writeleaf = formatkeyvalue(key, value);
		synchronized (Fileutil.synckey(String.valueOf(hashkey(key)))) {
			ByteBuffer root = ByteBuffer.allocate(1);
			long rootstartposition = hashleaf(key) * 32;
			Fileutil.read(rootfile(filter), rootstartposition, root);
			if (root.array()[0] == 0) {
				String leafile = UUID.randomUUID().toString().replaceAll("-", "");
				ByteBuffer writeroot = ByteBuffer.allocate(32);
				writeroot.put(STATIC.tobytes(leafile));
				Fileutil.write(rootfile(filter), rootstartposition, writeroot);
				Fileutil.write(leafolder(filter).resolve(leafile),
						hashvalue(key, distributions) * (capacityvalue + capacitykeymax), writeleaf);
			} else {
				ByteBuffer readleafile = ByteBuffer.allocate(32);
				Fileutil.read(rootfile(filter), rootstartposition, readleafile);
				String leafile = STATIC.tostring(readleafile.array());
				if (!Files.exists(leafolder(filter).resolve(leafile))) {
					Fileutil.write(leafolder(filter).resolve(leafile),
							hashvalue(key, distributions) * (capacityvalue + capacitykeymax), writeleaf);
				} else {
					ByteBuffer leaf = ByteBuffer.allocate(1);
					long leafstartposition = hashvalue(key, distributions) * (capacityvalue + capacitykeymax);
					Fileutil.read(leafolder(filter).resolve(leafile), leafstartposition, leaf);
					if (leaf.array()[0] == 0) {
						Fileutil.write(leafolder(filter).resolve(leafile),
								hashvalue(key, distributions) * (capacityvalue + capacitykeymax), writeleaf);
					} else {
						ByteBuffer readvalue = ByteBuffer.allocate(capacityvalue + capacitykeymax);
						Fileutil.read(leafolder(filter).resolve(leafile), leafstartposition, readvalue);
						boolean duplicate = true;
						byte[] val = readvalue.array();
						for (int i = 0; i < key.length; i++) {
							if (key[i] != val[capacityvalue + i]) {
								duplicate = false;
								break;
							}
						}
						if (duplicate) {
							throw new Exception("duplicate");
						} else {
							if (val[32] == 0) {
								String collisionfile = STATIC.tostring(Arrays.copyOf(val, 32));
								if (!Files.exists(collisionfolder(filter).resolve(collisionfile))) {
									Fileutil.write(leafolder(filter).resolve(leafile),
											hashvalue(key, distributions) * (capacityvalue + capacitykeymax),
											writeleaf);
								} else {
									collision(collisionfolder(filter).resolve(collisionfile), null, writeleaf, null);
								}
							} else {
								String collisionfile = UUID.randomUUID().toString().replaceAll("-", "");
								ByteBuffer writeleafcollision = ByteBuffer.allocate(capacityvalue + capacitykeymax);
								writeleafcollision.put(STATIC.tobytes(collisionfile));
								writeleafcollision.put(new byte[capacityvalue + capacitykeymax - 32]);
								Fileutil.write(leafolder(filter).resolve(leafile),
										hashvalue(key, distributions) * (capacityvalue + capacitykeymax),
										writeleafcollision);

								ByteBuffer writecollision = ByteBuffer.allocate(2 * (capacityvalue + capacitykeymax));
								writecollision.put(val);
								writecollision.put(writeleaf.array());
								Fileutil.write(collisionfolder(filter).resolve(collisionfile), 0, writecollision);
							}
						}
					}
				}
			}
		}
	}

	public static byte[] read(String filter, byte[] key, int distributions) throws Exception {
		ByteBuffer root = ByteBuffer.allocate(1);
		long rootstartposition = hashleaf(key) * 32;
		Fileutil.read(rootfile(filter), rootstartposition, root);
		if (root.array()[0] == 0) {
			return null;
		} else {
			ByteBuffer readleafile = ByteBuffer.allocate(32);
			Fileutil.read(rootfile(filter), rootstartposition, readleafile);
			String leafile = STATIC.tostring(readleafile.array());
			if (!Files.exists(leafolder(filter).resolve(leafile))) {
				return null;
			} else {
				ByteBuffer leaf = ByteBuffer.allocate(1);
				long leafstartposition = hashvalue(key, distributions) * (capacityvalue + capacitykeymax);
				Fileutil.read(leafolder(filter).resolve(leafile), leafstartposition, leaf);
				if (leaf.array()[0] == 0) {
					return null;
				} else {
					ByteBuffer readvalue = ByteBuffer.allocate(capacityvalue + capacitykeymax);
					Fileutil.read(leafolder(filter).resolve(leafile), leafstartposition, readvalue);
					boolean samekey = true;
					byte[] val = readvalue.array();
					for (int i = 0; i < key.length; i++) {
						if (key[i] != val[capacityvalue + i]) {
							samekey = false;
							break;
						}
					}
					if (samekey) {
						return Arrays.copyOf(val, capacityvalue);
					} else {
						if (val[32] == 0) {
							String collisionfile = STATIC.tostring(Arrays.copyOf(val, 32));
							if (!Files.exists(collisionfolder(filter).resolve(collisionfile))) {
								return null;
							} else {
								ByteBuffer returnvalue = ByteBuffer.allocate(capacityvalue);
								collision(collisionfolder(filter).resolve(collisionfile), key, null, returnvalue);
								byte[] rb = returnvalue.array();
								if (rb[0] == 0) {
									return null;
								} else {
									return rb;
								}
							}
						} else {
							return null;
						}
					}
				}
			}
		}
	}

}
