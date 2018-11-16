package com.zdd.bdc.biz;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.main.Startdatadig;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Fileutil;
import com.zdd.bdc.server.util.SS;
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

		String period = Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG)
				.read(digname + ".period");
		if (period != null && !period.trim().isEmpty()) {
			String[] startend = SS.splitfromto(period);
			Date start = null;
			Date end = null;
			try {
				start = SS.FORMAT_yMd.parse(startend[0]);
				end = SS.FORMAT_yMd.parse(startend[1]);
			} catch (Exception e) {
				end = start;
			}
			if (start != null && end != null) {
				Map<String, String> sortingserverswithinperiod = new Hashtable<String, String>();
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				boolean isthisserverincluded = false;
				while (cal.getTime().compareTo(end) <= 0) {
					String servers = Configclient.getinstance(namespace, CS.REMOTE_CONFIG_BIGDATA)
							.read(SS.FORMAT_yMd.format(cal.getTime()));
					if (servers == null || servers.trim().isEmpty()) {
						break;
					} else {
						String[] ipports = CS.splitenc(servers);
						for (String ipportstr : ipports) {
							try {
								String[] ipport = CS.splitiport(ipportstr);
								if (ipport[0].equals(ip) && Startdatadig.sortserverport(ipport[1]).equals(port)) {
									isthisserverincluded = true;
								}
								ipport[1] = Startdatadig.sortserverport(ipport[1]);
								sortingserverswithinperiod.put(ipportstr, CS.splitiport(ipport[0], ipport[1]));
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
				if (sortingserverswithinperiod.isEmpty()) {
					System.out.println(new Date() + " ==== no sorting servers for ["+digname+"]");
				} else {
					if (!isthisserverincluded) {
						System.out.println(new Date() + " ==== server ip [" + ip + "] port[" + port
								+ "] is not included [" + sortingserverswithinperiod + "] for ["+digname+"]");
					} else {
						Path targetfolder = null;
						try {
							targetfolder = Filekvutil.datafolder(namespace, table, col);
						} catch (Exception e) {
							System.out.println(new Date() + " ==== due to below exception terminated digging ["
									+ digname + "][" + namespace + "][" + table + "][" + col + "]");
							e.printStackTrace();
							return;
						}
						String filter = Configclient.getinstance(namespace, SS.REMOTE_CONFIG_DIG)
								.read(digname + ".filter");
						String[] filterarr = null;
						try {
							filterarr = CS.splitenc(filter);
						} catch (Exception e) {
							//do nothing
						}
						final String[] filters = filterarr;
						System.out.println(new Date() + " ==== started digging [" + digname + "][" + namespace + "]["
								+ table + "][" + col + "] with filter ["+filter+"]");

						String[] datafiles = targetfolder.toFile().list();
						for (String datafile : datafiles) {
							Fileutil.walkdata(targetfolder.resolve(datafile), new Filedatawalk() {

								@Override
								public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
										boolean isv1deleted, byte[] v2, boolean isv2deleted) {
									if (isv1deleted || isv2deleted) {
										return null;
									} else {
										String k = CS.tostring(v1);
										Long amount = null;
										try {
											amount = Long.parseLong(CS.tostring(v2));
										} catch (Exception e) {
											amount = (long) CS.tostring(v2)
													.compareTo(SS.SORT_COMPARE_TO_STRING);
										}
										
										Sortfactory.start(ip, port, sortingserverswithinperiod, 
												new Sortinputimpl(digname, namespace, table, col, null, version), output);
										
										if (Arrays.equals(value1, v1)) {
											return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
													Filedatawalkresult.DATA_REPLACE, value1, newvalue2);
										} else {
											// ignore the data
											return null;
										}
									}
								}
								
							}, true);
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
	
	public static String getfilters(String key, String namespace, String digname, int bigfilehash) throws Exception {
		String filterconfig = Configclient.getinstance(namespace, SS.REMOTE_CONFIG_DIG)
				.read(digname + ".filter");
		if (filterconfig==null||filterconfig.trim().isEmpty()) {
			return SS.SORT_ALL_FOLDER;
		} else {
			String[] t = CS.splitenc(filterconfig);
			String ns = t[0];
			Vector<String> filterarray = new Vector<String>((t.length-1)/2);
			for (int i=1;i<t.length;i+=2) {
				String table = t[i];
				String col = t[i+1];
				String f = Filekvutil.dataread(key, ns, table, col, bigfilehash);
				if (f==null||f.trim().isEmpty()) {
					throw new Exception("emptyfilter");
				} else {
					filterarray.add(f);
				}
			}
			String[] filterarr = new String[filterarray.size()];
			filterarray.toArray(filterarr);
			return CS.splitenc(filterarr);
		}
	}
	
}
