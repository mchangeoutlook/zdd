package com.zdd.bdc.biz;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filekvutil;

public class Digactive extends Thread {

	private String ip = null;
	private String port = null;
	private int bigfilehash = 0;

	public Digactive(String theip, String theport, int thebigfilehash) {
		ip = theip;
		port = theport;
		bigfilehash = thebigfilehash;
	}

	private Map<String, String> insameminute = new Hashtable<String, String>(1);

	@Override
	public void run() {

		System.out.println(new Date() + " ==== activating digging");
		while (Configclient.running) {
			if (Files.exists(STATIC.LOCAL_DATAFOLDER) && Files.isDirectory(STATIC.LOCAL_DATAFOLDER)) {
				Date now = new Date();
				String dayinterval = "D" + new SimpleDateFormat("HHmm").format(now);

				Calendar cal = Calendar.getInstance();
				cal.setTime(now);
				int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
				String weekinterval = "W" + w + new SimpleDateFormat("HHmm").format(now);

				if (insameminute.get(dayinterval) == null) {
					insameminute.clear();
					insameminute.put(dayinterval, dayinterval);
					new Thread(new Runnable() {

						@Override
						public void run() {
							active(ip, port, bigfilehash, weekinterval, dayinterval);
						}

					}).start();
				} else {
					// do nothing
				}
			} else {
				// do nothing
			}
			if (Configclient.running) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// do nothing;
				}
			} else {
				// do nothing
			}
		}
		System.out.println(new Date() + " ==== stopped activating digging");
	}

	private static void active(String ip, String port, int bigfilehash, String weekinterval, String dayinterval) {
		String[] digs = null;
		String active = Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG).read("active");
		if (active != null && !active.trim().isEmpty()) {
			try {
				digs = STATIC.splitenc(active);
			} catch (Exception e) {
				System.out.println(new Date() + " ==== wrong active config [" + active + "]");
			}
		} else {
			// do nothing
		}
		if (digs != null) {
			Date now = new Date();
			String version = STATIC.yMd_FORMAT(now);
			for (String digname : digs) {
				String sort = Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG)
						.read(digname + ".sort");
				String[] nstbcol = null;
				try {
					nstbcol = STATIC.splitenc(sort);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== wrong sort config [" + sort + "] for [" + digname + "]");
				}
				if (nstbcol != null) {
					Path folder = null;
					try {
						folder = Filekvutil.datafolder(nstbcol[0], nstbcol[1], nstbcol[2]);
					} catch (Exception e) {
						System.out.println(new Date() + " ==== target data folder error before sort [" + sort
								+ "] for [" + digname + "]");
						e.printStackTrace();
					}
					if (folder != null && Files.exists(folder) && Files.isDirectory(folder)) {
						String interval = Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG)
								.read(digname + ".interval");
						if (interval == null || !interval.startsWith("W") && !interval.startsWith("D")
								|| interval.startsWith("D") && interval.length() != 5
								|| interval.startsWith("W") && interval.length() != 6) {
							System.out.println(new Date() + " ==== wrong interval config [" + interval + "] for ["
									+ digname + "]");
						} else if (dayinterval.equals(interval) || weekinterval.equals(interval)) {
							addremoveactive(digname, new Digging(ip, port, digname, nstbcol[0], nstbcol[1], nstbcol[2],
									bigfilehash, version));
						} else {
							//do nothing
						}
					} else {
						System.out.println(
								new Date() + " ==== invalid data folder [" + folder + "] for [" + digname + "]");
					}
				}
			}
		} else {
			// do nothing
		}
	}

	private static Map<String, Digging> activedig = new Hashtable<String, Digging>();

	public static synchronized void addremoveactive(String digname, Digging digging) {
		if (activedig.get(digname) == null && digging != null) {
			activedig.put(digname, digging);
			digging.start();
		} else if (digging == null) {
			activedig.remove(digname);
			System.out.println(new Date() + " ==== removed dig [" + digname + "]");
		}
	}

}
