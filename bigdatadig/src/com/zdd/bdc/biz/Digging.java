package com.zdd.bdc.biz;

import java.nio.file.Path;
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
						
						System.out.println(new Date() + " ==== started digging [" + digname + "][" + namespace + "]["
								+ table + "][" + col + "]");

						String[] datafiles = targetfolder.toFile().list();
						for (String datafile : datafiles) {
							try {
								Fileutil.walkdata(targetfolder.resolve(datafile), new Filedatawalk() {

									@Override
									public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
											boolean isv1deleted, byte[] v2, boolean isv2deleted) {
										if (isv1deleted || isv2deleted) {
											return null;
										} else {
											String key = CS.tostring(v1);
											String filters = null;
											try {
												filters = Digging.getfilters(key, namespace, digname, bigfilehash);
											}catch(Exception e) {
												System.out.println(new Date() + " ==== ignore digging [" + digname + "][" + namespace + "]["
														+ table + "][" + col + "] due to filters error of key=["+key+"], value=["+CS.tostring(v2)+"], continue to next data");
												e.printStackTrace();
											}
											if (filters != null) {
												Sortfactory.start(ip, Integer.parseInt(port), sortingserverswithinperiod.values(), 
														new Sortinputimpl(digname, namespace, table, col, filters, version, bigfilehash),
														new Sortoutputimpl(bigfilehash));
											} else {
												//do nothing
											}
											return null;
										}
									}
									
								}, true);
							} catch (Exception e) {
								System.out.println(new Date() + " ==== ignore digging [" + digname + "][" + namespace + "]["
										+ table + "][" + col + "] due to error walking ["+datafile+"], continue to next data file");
								e.printStackTrace();
							}
						}
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
