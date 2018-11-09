package com.zdd.bdc.sort.local;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.sort.distribute.Sortcheck;
import com.zdd.bdc.sort.distribute.Sortdistribute;
import com.zdd.bdc.sort.util.Sortstatus;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortfactory {	
	
	public static Map<Path, Sortdistribute> sortdistributes = new Hashtable<Path, Sortdistribute>();
	public static Map<Path, Thread> sortings = new Hashtable<Path, Thread>();
	
	public static void clear(Path sortingfolder, String status) {
		Sortstatus.set(sortingfolder, status);
		sortings.remove(sortingfolder);
		try {
			sortdistributes.remove(sortingfolder).clear();
		}catch(Exception e) {
			//do nothing
		}
	}
	
	public synchronized static void start(String ip, int port, Vector<String> sortingservers, Sortinput input, Sortoutput output) throws Exception {
		Path sortingfolder = input.sortingfolder();
		if (sortings.get(sortingfolder) == null) {
			sortings.put(sortingfolder, 
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println(new Date() + " ==== started sorting local [" + sortingfolder + "]");
						input.datasource();
						System.out.println(new Date() + " ==== done sorting local [" + sortingfolder + "]");

						System.out
								.println(new Date() + " ==== started distributing sort folder [" + sortingfolder + "]");

						// check included sorting server:
						Vector<String> validsortingservers = new Vector<String>(sortingservers.size());
						for (String ipport : sortingservers) {
							Map<String, Object> params = new Hashtable<String, Object>(6);
							params.put(CS.PARAM_KEY_KEY, sortingfolder);
							params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
							String[] iport = CS.splitiport(ipport);
							int check = (Integer) Objectutil.convert(Theclient.request(iport[0],
									Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
							if (check == Sortcheck.SORT_INCLUDED||check==Sortdistribute.DISTRIBUTE_CONTINUE) {
								validsortingservers.add(ipport);
							} else if (check == Sortcheck.SORT_NOTINCLUDED) {
								// do nothing
							} else {
								throw new Exception("error sort check: [" + check + "] of [" + sortingfolder
										+ "] from [" + ipport + "]");
							}
						}
						Sorthouse.sortdistributes.put(sortingfolder, new Sortdistribute(ip, port, input.isasc(), validsortingservers, sortingfolder));
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Sorthouse.sortdistributes.get(sortingfolder).startinathread();
								} catch (Exception e) {
									System.out.println(
											new Date() + " ==== error when distributing sort folder [" + sortingfolder + "]");
									e.printStackTrace();
								}
							}
							
						}).start();
					} catch (Exception e) {
						System.out.println(
								new Date() + " ==== error when starting to distribute sort folder [" + sortingfolder + "]");
						e.printStackTrace();
						Sorthouse.sortdistributes.put(sortingfolder, new Sortdistribute(ip, port, input.isasc(), null, sortingfolder));
					}
				}

			});
			sortings.get(sortingfolder).start();
		} else {
			// do nothing
		}
	}
}
