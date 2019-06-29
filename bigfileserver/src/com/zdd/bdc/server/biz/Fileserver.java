package com.zdd.bdc.server.biz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;

public class Fileserver implements Theserverprocess {
	
	@Override
	public void init(String ip, int port, int bigfilehash, Map<String, Object> additionalserverconfig) {

	}

	@Override
	public byte[] request(byte[] b) throws Exception {
		if (b!=null&&b.length!=0) {
			String path = STATIC.tostring(b);
			if (path.endsWith(STATIC.DELETE_BIGFILE_SUFFIX)) {
				path = path.substring(0, path.length()-STATIC.DELETE_BIGFILE_SUFFIX.length());
				Path targetpath = path.startsWith("/")?STATIC.LOCAL_DATAFOLDER.resolve(path.replaceFirst("/", "")):STATIC.LOCAL_DATAFOLDER.resolve(path);
				targetpath.toFile().delete();
			}
		}
		return null;
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		if (param!=null&&param.length!=0) {
			String paramstr = STATIC.tostring(param);
			if (paramstr.endsWith(STATIC.WRITE_BIGFILE_SUFFIX)) {
				String paramval = paramstr.substring(0, paramstr.lastIndexOf(STATIC.WRITE_BIGFILE_SUFFIX));
				return new Inputprocess() {
					private Path targetpath = paramval.startsWith("/")?STATIC.LOCAL_DATAFOLDER.resolve(paramval.replaceFirst("/", "")):STATIC.LOCAL_DATAFOLDER.resolve(paramval);
					@Override
					public void process(byte[] b) throws Exception {
						if (!Files.exists(targetpath)&&targetpath.getParent()!=null&&!Files.exists(targetpath.getParent())) {
							Files.createDirectories(targetpath.getParent());
						}
						Files.write(targetpath, b, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
					}
				};
			}
		}
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		if (param!=null&&param.length!=0) {
			String paramstr = STATIC.tostring(param);
			if (paramstr.endsWith(STATIC.READ_BIGFILE_SUFFIX)) {
				String path = paramstr.substring(0, paramstr.lastIndexOf(STATIC.READ_BIGFILE_SUFFIX));
				if (path.startsWith("/")) {
					path = path.replaceFirst("/", "");
				}
				return Files.newInputStream(STATIC.LOCAL_DATAFOLDER.resolve(path));
			}
		}
		return null;
	}

	@Override
	public int requestoutputbytes(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return 10240;
	}

}
