package com.zdd.bdc.sort.local;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.util.SS;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortfactory {
	private static Map<String, Sortinput> SI = new Hashtable<String, Sortinput>();
	private static Map<String, Sortoutput> SO = new Hashtable<String, Sortoutput>();
	private static Map<String, BufferedReader> BR = new Hashtable<String, BufferedReader>();
	
	public static Sortoutput get(Path sortingfolder) {
		return SO.get(sortingfolder.toString());
	}
	
	public static synchronized void stop(Path sortingfolder) {
		SI.remove(sortingfolder.toString());
		SO.remove(sortingfolder.toString());
		try {
			BR.get(sortingfolder.toString()).close();
		}catch(Exception e) {
			//do nothing
		}
		BR.remove(sortingfolder.toString());
		System.out.println(new Date()+" ==== stopped distributing sort folder ["+sortingfolder+"]");
	}
	
	public static String next(Path sortingfolder) throws Exception {
		synchronized(SS.syncfile(sortingfolder)) {
			String line = BR.get(sortingfolder.toString()).readLine();
			if (line==null) {
				stop(sortingfolder);
			} else {
				//do nothing
			}
			return line;
		}
	}
	
	public synchronized static void start(Vector<String> sortingservers, Sortinput si, Sortoutput so, Sortstatus ss) throws Exception{
		Path sortingfolder = si.sortingfolder();
		final boolean isasc = si.isasc();
		if (SI.get(sortingfolder.toString())==null) {
			SI.put(sortingfolder.toString(), si);
			SO.put(sortingfolder.toString(), so);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						si.datasource();
						Path mergedfile = Sortutil.sortmerge(isasc, sortingfolder);
						BR.put(sortingfolder.toString(), Files.newBufferedReader(mergedfile, Charset.forName("UTF-8")));
						
						Sortcontrol.to(sortingfolder, Sortcontrol.CONTINUE);
						System.out.println(new Date()+" ==== started distributing sort folder ["+sortingfolder+"]");
						
						//check included sorting server:
						Vector<String> validsortingservers = new Vector<String>(sortingservers.size());
						for (String ipport:sortingservers) {
							Map<String, Object> params = new Hashtable<String, Object>(6);
							params.put(CS.PARAM_KEY_KEY, sortingfolder);
							params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
							String[] iport = CS.splitiport(ipport);
							int sortstatus = (Integer)Objectutil.convert(Theclient.request(iport[0], 
									Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
							if (sortstatus==Sortstatus.MERGED||sortstatus == Sortstatus.SORTING) {
								validsortingservers.add(ipport);
							} else if (sortstatus==Sortstatus.NOTFOUND){
								//do nothing
							} else {
								throw new Exception("error sort status: ["+sortstatus+"] from ["+ipport+"]");
							}
						}
						
						//distribute the first data:
						for (String ipport:validsortingservers) {
							Map<String, Object> params = new Hashtable<String, Object>(6);
							params.put(CS.PARAM_KEY_KEY, sortingfolder);
							params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
							params.put(CS.PARAM_DATA_KEY, Sortfactory.next(sortingfolder));
							String[] iport = CS.splitiport(ipport);
							Theclient.request(iport[0], 
									Integer.parseInt(iport[1]), Objectutil.convert(params), null, null);
						}
					} catch (Exception e) {
						Sortcontrol.to(sortingfolder, Sortcontrol.TERMINATE);
						System.out.println(new Date()+" ==== error when distributing sort folder ["+sortingfolder+"]");
						e.printStackTrace();
						stop(sortingfolder);
					}
				}
				
			}).start();
		} else {
			//do nothing
		}
	}
}
