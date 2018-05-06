package com.zdd.bdc.ex;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Theclienttest {
	private static long k = 0;
	public static void main(String[] s) throws Exception {
		//while(true) {
			for (int i=0;i<1;i++) {
				k++;
				final int j = i;
				Thread t = new Thread(new Runnable() {
	
					@Override
					public void run() {
						byte[] res;
						try {
							res = Theclient.request("10.0.0.9", 9999, ("abc你好"+j).getBytes("UTF-8"), Files.newInputStream(Paths.get("logo1.jpg")));
							System.out.println(k+"="+j+"["+new String(res, "UTF-8")+"]");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
				t.start();
				t.join();
			}
		//}
	}
}
