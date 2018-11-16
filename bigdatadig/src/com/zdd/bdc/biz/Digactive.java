package com.zdd.bdc.biz;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.SS;

public class Digactive extends Thread {

	private String ip = null;
	private String port = null;
	private int bigfilehash = 0;

	public Digactive(String theip, String theport, int thebigfilehash) {
		ip = theip;
		port = theport;
		bigfilehash = thebigfilehash;
	}

	@Override
	public void run() {
		String ipportpending = null;
		try {
			ipportpending = CS.splitenc(ip, port);
		}catch(Exception e) {
			System.out.println(new Date() + " ==== terminated digging due to below exception");
			e.printStackTrace();
			return;
		}
		System.out.println(new Date() + " ==== activating digging");
		while (!SS.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
						.read(ipportpending))) {
			if (Files.exists(SS.LOCAL_DATAFOLDER) && Files.isDirectory(SS.LOCAL_DATAFOLDER)) {
					
				String[] digs = null;
				String active = Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG).read("active");
				try {
					digs = CS.splitenc(active);
				}catch(Exception e) {
					System.out.println(new Date()+" ==== wrong active config ["+active+"]");
				}
				if (digs!=null) {
					Date now = new Date();
					String version = SS.FORMAT_yMd.format(now);
					for (String digname:digs) {
						String sort = Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG).read(digname+".sort");
						String[] nstbcol = null;
						try {
							nstbcol = CS.splitenc(sort);
						}catch(Exception e) {
							System.out.println(new Date()+" ==== wrong sort config ["+sort+"] for ["+digname+"]");
						}
						if (nstbcol!=null) {
							Path folder = null;
							try {
								folder = Filekvutil.datafolder(nstbcol[0], nstbcol[1], nstbcol[2]);
							}catch(Exception e) {
								System.out.println(new Date()+" ==== target data folder error before sort ["+sort+"] for ["+digname+"]");
								e.printStackTrace();
							}
							if (folder!=null&&Files.exists(folder)&&Files.isDirectory(folder)) {
								String interval = Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG).read(digname+".interval");
								if (interval!=null&&interval.length()>3) {
									if (interval.startsWith("D")) {
										if (interval.substring(1).equals(new SimpleDateFormat("HHmm").format(now))) {
											addremoveactive(digname, new Digging(ip, port, digname, nstbcol[0], nstbcol[1], nstbcol[2], bigfilehash, version));
										}
									} else if (interval.startsWith("W")) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(now);
										int w = cal.get(Calendar.DAY_OF_WEEK)-1;
										if (interval.startsWith("W"+w)&&interval.substring(2).equals(new SimpleDateFormat("HHmm").format(now))) {
											addremoveactive(digname, new Digging(ip, port, digname, nstbcol[0], nstbcol[1], nstbcol[2], bigfilehash, version));
										}
									} else {
										System.out.println(new Date()+" ==== wrong interval config ["+interval+"] for ["+digname+"]");
									}
								} else {
									System.out.println(new Date()+" ==== no interval config ["+interval+"] for ["+digname+"]");
								}
							} else {
								System.out.println(new Date()+" ==== invalid data folder ["+folder+"] for ["+digname+"]");
							}
						}
					}
				} else {
					//do nothing
				}
				
			} else {
				//do nothing
			}
			if (!SS.REMOTE_CONFIGVAL_PENDING
					.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
							.read(ipportpending))) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					// do nothing;
				}
			} else{
				//do nothing
			}
		}
		System.out.println(new Date() + " ==== stopped activating digging");
	}

	private static Map<String, Digging> activedig = new Hashtable<String, Digging>();
	public static synchronized void addremoveactive(String digname, Digging digging) {
		if (activedig.get(digname)==null&&digging!=null) {
			activedig.put(digname, digging);
			digging.start();
		} else if (digging==null) {
			activedig.remove(digname);
		}
	}
	
}
