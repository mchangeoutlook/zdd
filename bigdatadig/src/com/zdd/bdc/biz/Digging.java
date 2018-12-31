package com.zdd.bdc.biz;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.main.Startdatadig;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.sort.local.Sortfactory;

public class Digging extends Thread {

	private String ip = null;
	private String port = null;
	private String digname = null;
	private String namespace = null;
	private String table = null;
	private String col = null;
	private String version = null;
	private int bigfilehash = 0;

	public Digging(String theip, String theport, String thedigname, String thenamespace, String thetable, String thecol,
			int thebigfilehash, String theversion) {
		ip = theip;
		port = theport;
		digname = thedigname;
		namespace = thenamespace;
		table = thetable;
		col = thecol;
		bigfilehash = thebigfilehash;
		version = theversion;
	}

	@Override
	public void run() {

		String period = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_DIG).read(digname + ".period");
		if (period != null && !period.trim().isEmpty()) {
			String[] startend = STATIC.splitfromto(period);
			Date start = null;
			Date end = null;
			try {
				start = STATIC.yMd_FORMAT(startend[0]);
				end = STATIC.yMd_FORMAT(startend[1]);
			} catch (Exception e) {
				end = start;
			}
			if (start != null && end != null) {
				Map<String, String> sortingserverswithinperiod = new Hashtable<String, String>();
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				boolean isthisserverincluded = false;
				while (cal.getTime().compareTo(end) <= 0) {
					String servers = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGDATA)
							.read(STATIC.yMd_FORMAT(cal.getTime()));
					if (servers == null || servers.trim().isEmpty()) {
						if (sortingserverswithinperiod.isEmpty()) {
							// do nothing
						} else {
							break;
						}
					} else {
						String[] ipports = STATIC.splitenc(servers);
						for (String ipportstr : ipports) {
							String[] ipport = STATIC.splitiport(ipportstr);
							if (ipport[0].equals(ip) && Startdatadig.sortserverport(ipport[1]).equals(port)) {
								isthisserverincluded = true;
							}
							ipport[1] = Startdatadig.sortserverport(ipport[1]);
							sortingserverswithinperiod.put(ipportstr, STATIC.splitiport(ipport[0], ipport[1]));
						}
					}
					cal.add(Calendar.DATE, 1);
				}
				if (sortingserverswithinperiod.isEmpty()) {
					System.out.println(new Date() + " ==== no sorting servers for [" + digname + "] during period ["
							+ period + "]");
				} else {
					if (!isthisserverincluded) {
						System.out.println(new Date() + " ==== server ip [" + ip + "] port[" + port
								+ "] is not included [" + sortingserverswithinperiod + "] for [" + digname + "]");
					} else {
						Path targetfolder = Filekvutil.datafolder(namespace, table, col);
						System.out.println(new Date() + " ==== started digging [" + digname + "][" + namespace + "]["
								+ table + "][" + col + "]");

						String[] datafiles = targetfolder.toFile().list();

						Map<String, String> sortfilters = new Hashtable<String, String>();
						for (String datafile : datafiles) {
							StringBuffer errors = new StringBuffer();
							try {
								Filekvutil.walkdata(targetfolder.resolve(datafile), new Filedatawalk() {

									@Override
									public Filedatawalkresult data(long datasequence, long dataseqincludedeleted,
											byte[] v1, boolean isv1deleted, byte[] v2, boolean isv2deleted) {
										if (!Configclient.running) {
											errors.append(new Date() + " ==== shutdown this server [" + ip + "][" + port
													+ "]");
											return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
													Filedatawalkresult.DATA_DONOTHING, null, null);
										} else {
											if (isv1deleted || isv2deleted) {
												return null;
											} else {
												String key = STATIC.tostring(v1);
												String filters = null;
												try {
													filters = Digging.getfilters(key, namespace, digname, bigfilehash);
												} catch (Exception e) {
													errors.append(new Date() + " ==== filters error of key=[" + key
															+ "], value=[" + STATIC.tostring(v2) + "] due to "
															+ STATIC.strackstring(e));
													return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
															Filedatawalkresult.DATA_DONOTHING, null, null);
												}
												if (filters != null) {
													sortfilters.put(filters, filters);
												} else {
													// do nothing
												}
												return null;
											}
										}
									}

								}, true);
								if (errors.length() != 0) {
									throw new Exception(errors.toString());
								}
							} catch (Exception e) {
								System.out.println(new Date() + " ==== terminate digging [" + digname + "][" + namespace
										+ "][" + table + "][" + col + "] due to error walking [" + datafile + "]");
								e.printStackTrace();
								Digactive.addremoveactive(digname, null);
								return;
							}
						}
						if (sortfilters.isEmpty()) {
							System.out.println(new Date() + " ==== ignore digging [" + digname + "][" + namespace + "]["
									+ table + "][" + col + "] due to no filters [" + sortfilters + "]");
						} else {
							for (String filters : sortfilters.values()) {
								Sortfactory.start(ip, Integer.parseInt(port), sortingserverswithinperiod.values(),
										new Sortinputimpl(digname, namespace, table, col, filters, version,
												bigfilehash),
										new Sortoutputimpl(bigfilehash));
							}
						}
					}
				}
			} else {
				System.out.println(new Date() + " ==== wrong period config [" + period + "] for [" + digname + "]");
			}
		} else {
			System.out.println(new Date() + " ==== no period config [" + period + "] for [" + digname + "]");
		}
		Digactive.addremoveactive(digname, null);
	}

	public static String getfilters(String key, String namespace, String digname, int bigfilehash) throws Exception {
		String filterconfig = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_DIG).read(digname + ".filter");
		Vector<String> filterarray = new Vector<String>(10);
		filterarray.add(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIG_DIG).read(digname + ".sequence"));
		if (filterconfig == null || filterconfig.trim().isEmpty()) {
			// do nothing;
		} else {
			String[] t = STATIC.splitenc(filterconfig);
			if (t.length == 1) {
				filterarray.add(filterconfig);
			} else {
				for (int i = 0; i < t.length; i += 2) {
					String table = t[i];
					String col = t[i + 1];
					String f = Filekvutil.dataread(key, namespace, table, col, bigfilehash);
					if (f == null || f.trim().isEmpty()) {
						throw new Exception("no filter value in col=[" + col + "],table=[" + table + "], namespace=["
								+ namespace + "]");
					} else {
						filterarray.add(f);
					}
				}
			}
		}
		return STATIC.splitenc(filterarray);
	}

}
