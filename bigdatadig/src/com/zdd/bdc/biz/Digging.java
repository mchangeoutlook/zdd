package com.zdd.bdc.biz;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Texti;
import com.zdd.bdc.server.util.Fileutil;

public class Digging extends Thread {

	private String ip = null;
	private String port = null;
	private String digname = null;
	private String namespace = null;
	private String table = null;
	private String col = null;
	private String version = null;
	private int bigfilehash = 0;

	private Map<String, Sorting> sorts = new Hashtable<String, Sorting>();

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

		String period = Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_DIG)
				.read(digname + ".period");
		if (period != null && !period.trim().isEmpty()) {
			String[] startend = STATIC.fromto(period);
			Date start = null;
			Date end = null;
			try {
				start = STATIC.FORMAT_yMd.parse(startend[0]);
				end = STATIC.FORMAT_yMd.parse(startend[1]);
			} catch (Exception e) {
				end = start;
			}
			if (start != null && end != null) {
				Map<String, String[]> sortingservers = new Hashtable<String, String[]>();
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				boolean isthisserverincluded = false;
				while (cal.getTime().compareTo(end) <= 0) {
					String servers = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA)
							.read(STATIC.FORMAT_yMd.format(cal.getTime()));
					if (servers == null || servers.trim().isEmpty()) {
						break;
					} else {
						String[] ipports = STATIC.split(servers);
						for (String ipportstr : ipports) {
							try {
								String[] ipport = STATIC.splitenc(ipportstr);
								if (ipport[0].equals(ip) && ("1" + ipport[1]).equals(port)) {
									isthisserverincluded = true;
								}
								ipport[1] = "1" + ipport[1];
								sortingservers.put(ipportstr, ipport);
							} catch (Exception e) {
								System.out.println(new Date() + " ==== due to below exception terminated digging ["
										+ digname + "][" + namespace + "][" + table + "][" + col + "] wrong ip port ["+ipportstr+"]");
								e.printStackTrace();
								return;
							}
						}
					}
					cal.add(Calendar.DATE, 1);
				}
				if (sortingservers.isEmpty()) {
					System.out.println(new Date() + " ==== no sorting servers for ["+digname+"]");
				} else {
					if (!isthisserverincluded) {
						System.out.println(new Date() + " ==== server ip [" + ip + "] port[" + port
								+ "] is not included [" + sortingservers + "] for ["+digname+"]");
					} else {
						Path targetfolder = null;
						try {
							targetfolder = Fileutil.targetfolder(namespace, table, col);
						} catch (Exception e) {
							System.out.println(new Date() + " ==== due to below exception terminated digging ["
									+ digname + "][" + namespace + "][" + table + "][" + col + "]");
							e.printStackTrace();
							return;
						}
						String filter = Configclient.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_DIG)
								.read(digname + ".filter");
						String[] filterarr = null;
						try {
							filterarr = STATIC.split(filter);
						} catch (Exception e) {
							//do nothing
						}
						final String[] filters = filterarr;
						System.out.println(new Date() + " ==== started digging [" + digname + "][" + namespace + "]["
								+ table + "][" + col + "] with filter ["+filter+"]");

						String[] datafiles = targetfolder.toFile().list();
						for (String datafile : datafiles) {
							SeekableByteChannel sbc = null;
							try {
								Fileutil.findkey(
										Files.newByteChannel(targetfolder.resolve(datafile), StandardOpenOption.READ),
										Files.size(targetfolder.resolve(datafile)), null, new Texti() {

											@Override
											public void process(byte[] key, byte[] value) throws Exception {
												String k = new String(key, STATIC.CHARSET_DEFAULT);
												Long amount = null;
												try {
													amount = Long.parseLong(new String(value, STATIC.CHARSET_DEFAULT));
												} catch (Exception e) {
													amount = (long) new String(value, STATIC.CHARSET_DEFAULT)
															.compareTo(STATIC.SORT_COMPARE_TO_STRING);
												}

												if (sorts.get(STATIC.SORT_ALL) == null) {
													sorts.put(STATIC.SORT_ALL,
															new Sorting(ip, port, digname, namespace, table, col,
																	STATIC.SORT_ALL, version, sortingservers.values()));
												}
												sorts.get(STATIC.SORT_ALL).sortoneserverintofiles(k, amount);
												
												if (filters!=null) {
													for (int i = 0; i < filters.length; i++) {
														Vector<String> filter = new Vector<String>(i+1);
														for (int j = 0; j <= i; j++) {
															String[] nstbcol = STATIC.splitenc(filters[i]);
															String f = new String(
																	Fileutil.read(k,
																			Fileutil.target(k, nstbcol[0],
																					nstbcol[1], nstbcol[2], bigfilehash)),
																	STATIC.CHARSET_DEFAULT);
															if (f==null||f.trim().isEmpty()) {
																filter.clear();
																break;
															}
															filter.add(f);
														}
														if (!filter.isEmpty()) {
															String[] filterarray = new String[filter.size()];
															filter.toArray(filterarray);
															String filterstr = STATIC.splitenc(filterarray);
															if (sorts.get(filterstr) == null) {
																sorts.put(filterstr, new Sorting(ip, port, digname, namespace, table, col,
																		filterstr, version, sortingservers.values()));
															}
															sorts.get(filterstr).sortoneserverintofiles(k, amount);
														} else {
															System.out.println(new Date() + " ==== gave up sorting [" + digname + "][" + namespace + "][" + 
																 table + "][" + col + "] with filter ["+filter+"]");
														}
													}
												}
											}
										});
							} catch (Exception e) {
								System.out.println(new Date() + " ==== error when processing datafile ["
										+ targetfolder.resolve(datafile).toAbsolutePath().toString() + "]");
								e.printStackTrace();
							} finally {
								if (sbc != null) {
									try {
										sbc.close();
									} catch (Exception e) {
										// do nothing
									}
								}
							}
						}
						for (Sorting sorting : sorts.values()) {
							try {
								sorting.start();
							} catch (Exception e) {
								System.out.println(new Date() + " ==== error when sorting into ["
										+ sorting.sortdonefile().toAbsolutePath().toString() + "]");
								e.printStackTrace();
							}
						}
						sorts.clear();
					}
				}
			} else {
				System.out.println(new Date() + " ==== wrong period config [" + period + "] for ["+digname+"]");
			}
		} else {
			System.out.println(new Date() + " ==== no period config [" + period + "] for ["+digname+"]");
		}
	}
	
}
