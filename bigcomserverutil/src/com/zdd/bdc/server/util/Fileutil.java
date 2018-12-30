package com.zdd.bdc.server.util;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Fileutil {
	private static final String charset = "UTF-8";

	// to avoid dead lock, sync file and key on different range.
	private static final int synchash = 10000;

	public static final String syncfile(Path tosync) {
		return String.valueOf(Math.abs(tosync.getFileName().toString().hashCode() % synchash) + synchash).intern();
	}

	public static final String synckey(String tosync) {
		return String.valueOf(Math.abs(tosync.hashCode() % synchash)).intern();
	}

	public static BufferedReader br(Path target) throws Exception {
		return Files.newBufferedReader(target, Charset.forName(charset));
	}

	public static void write(Path target, long startposition, ByteBuffer towrite) throws Exception {
		if (!Files.exists(target)) {
			Files.write(target, new byte[0], StandardOpenOption.CREATE);
		}
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
	public static void read(Path target, long startposition, ByteBuffer toread) throws Exception {
		SeekableByteChannel sbc = null;
		try {
			sbc = Files.newByteChannel(target, StandardOpenOption.READ);
			sbc.position(startposition);
			sbc.read(toread);
		} finally {
			if (sbc != null) {
				sbc.close();
			}
		}
	}
}
