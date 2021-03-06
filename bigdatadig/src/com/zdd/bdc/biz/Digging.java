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
import com.zdd.bdc.server.util.Filedatautil;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
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

		String period = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG).read(digname + ".period");
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
				String arraystr_date_servernum = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA)
						.read(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG).read(digname + ".app")
								+ STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX);
				String[] array_date_servernum = STATIC.splitenc(arraystr_date_servernum);
				Map<String, Long> date_servernum = new Hashtable<String, Long>();
				if (arraystr_date_servernum != null && !arraystr_date_servernum.trim().isEmpty()
						&& array_date_servernum != null) {
					try {
						for (int i = 0; i < array_date_servernum.length; i++) {
							String[] dateservernum = STATIC.splitenc(array_date_servernum[i]);
							date_servernum.put(STATIC.yMd_FORMAT(STATIC.yMd_FORMAT(dateservernum[0])),
									Long.parseLong(dateservernum[1]));
						}
					} catch (Exception e) {
						date_servernum.clear();
					}
				}
				if (!date_servernum.isEmpty()) {
					Map<String, String> sortingserverswithinperiod = new Hashtable<String, String>();
					Calendar cal = Calendar.getInstance();
					cal.setTime(start);
					boolean isthisserverincluded = false;
					while (cal.getTime().compareTo(end) <= 0) {

						Date targetdate = null;
						try {
							for (int i = 0; i < array_date_servernum.length; i++) {
								String[] dateservernum = STATIC.splitenc(array_date_servernum[i]);
								Date date = STATIC.yMd_FORMAT(dateservernum[0]);
								if (cal.getTime().equals(date)) {
									targetdate = date;
									break;
								} else if (cal.getTime().before(date)) {
									break;
								} else {
									targetdate = date;
								}
							}
						} catch (Exception e) {
							targetdate = null;
						}
						if (targetdate == null) {
							if (sortingserverswithinperiod.isEmpty()) {
								// do nothing
							} else {
								break;
							}
						} else {
							Long servernum = date_servernum.get(STATIC.yMd_FORMAT(targetdate));
							for (long i = 0; i < servernum; i++) {
								String ipportstr = Configclient
										.getinstance(namespace,
												STATIC.REMOTE_CONFIGFILE_BIGDATA)
										.read(STATIC
												.splitenc(Configclient
														.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG)
														.read(digname + ".app"), STATIC.yMd_FORMAT(targetdate))
												+ STATIC.REMOTE_CONFIGKEY_SERVERINDEXMIDDLE + i);
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
							Path targetfolder = Filedatautil.folder(namespace, table, col);
							System.out.println(new Date() + " ==== started digging [" + digname + "][" + namespace
									+ "][" + table + "][" + col + "]");

							String[] datafiles = targetfolder.toFile().list();

							Map<String, String> sortfilters = new Hashtable<String, String>();
							for (String datafile : datafiles) {
								StringBuffer errors = new StringBuffer();
								try {
									Filekvutil.walkdata(STATIC.keylength, targetfolder.resolve(datafile),
											new Filedatawalk() {

												@Override
												public Filedatawalkresult data(long datasequence,
														long dataseqincludedeleted, String key, String value,
														boolean isvaluedeleted) {
													if (!Configclient.running()) {
														errors.append(new Date() + " ==== shutdown this server [" + ip
																+ "][" + port + "]");
														return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
																Filedatawalkresult.DATA_DONOTHING, null, null);
													} else {
														if (isvaluedeleted) {
															return null;
														} else {
															String filters = null;
															try {
																filters = Digging.getfilters(key, namespace, digname,
																		bigfilehash);
															} catch (Exception e) {
																errors.append(
																		new Date() + " ==== filters error of key=["
																				+ key + "], value=[" + value
																				+ "] due to " + STATIC.stackstring(e));
																return new Filedatawalkresult(
																		Filedatawalkresult.WALK_TERMINATE,
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

											});
									if (errors.length() != 0) {
										throw new Exception(errors.toString());
									}
								} catch (Exception e) {
									System.out.println(
											new Date() + " ==== terminate digging [" + digname + "][" + namespace + "]["
													+ table + "][" + col + "] due to error walking [" + datafile + "]");
									e.printStackTrace();
									Digactive.addremoveactive(digname, null);
									return;
								}
							}
							if (sortfilters.isEmpty()) {
								System.out.println(new Date() + " ==== ignore digging [" + digname + "][" + namespace
										+ "][" + table + "][" + col + "] due to no filters [" + sortfilters + "]");
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
					System.out.println(new Date() + " ==== wrong app config for [" + digname + "]");
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
		String filterconfig = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG)
				.read(digname + ".filter");
		Vector<String> filterarray = new Vector<String>(10);
		filterarray.add(Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG).read(digname + ".sequence"));
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
					String f = Filedatautil.read(key, namespace, table, col, bigfilehash);
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
