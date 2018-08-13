package com.zdd.bdc.biz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import com.zdd.bdc.ex.Theclient;
import com.zdd.bdc.ex.Theclientprocess;
import com.zdd.bdc.util.STATIC;

public class Fileclient {

	private String path = null;

	private Fileclient(String thepath) {
		path = thepath;
	}

	public static Fileclient getinstance(String thepath) {
		return new Fileclient(thepath);
	}

	public void write(String key, InputStream requests) throws Exception {
		try {
			String[] iport = Bigclient.distributebigdata("pngbigto", key).split(STATIC.IP_SPLIT_PORT);
			Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes("UTF-8"), requests, null);
		} finally {
			requests.close();
		}
	}

	public String read(String key, Theclientprocess cp) throws Exception {
		String[] iport = Bigclient.distributebigdata("pngbigfrom", key).split(STATIC.IP_SPLIT_PORT);
		Theclient.request(iport[0], Integer.parseInt(iport[1]), path.getBytes("UTF-8"), null, cp);
		return key;
	}
	
	public static void main(String[] s) throws IOException, Exception {
		for (int i=0;i<100;i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int j=0	;j<45;j++) {
						String key = Bigclient.newbigdatakey();
						String path = "/test/"+UUID.randomUUID().toString();
						try {
							Fileclient.getinstance(path).write(key, Files.newInputStream(Paths.get("/Users/mido/Documents/青食H5.zip")));
						
						Fileclient.getinstance(path).read(key, new Theclientprocess() {
							String path = UUID.randomUUID().toString();
							@Override
							public void responses(byte[] b) throws Exception {
								Files.write(Paths.get("/Users/mido/Downloads/unicorn/unicorndemo/test/"+path+".zip"), b, StandardOpenOption.CREATE,StandardOpenOption.APPEND);
							}
							
						});
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
		
			}).start();
		}
	}

}
