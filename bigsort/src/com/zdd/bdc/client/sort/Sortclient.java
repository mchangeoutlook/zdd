package com.zdd.bdc.client.sort;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.server.util.Sortutil;

public class Sortclient {
	private static Map<String, Sortinput> SI = new Hashtable<String, Sortinput>();
	private static Map<String, Sortoutput> SO = new Hashtable<String, Sortoutput>();
	
	public static Sortoutput get(Path sortingfolder) {
		return SO.get(sortingfolder.toString());
	}
	
	public synchronized void start(Vector<String> sortingservers, Sortinput si, Sortoutput so, boolean isasc) throws Exception{
		Path sortingfolder = si.sortingfolder();
		if (SI.get(sortingfolder.toString())==null) {
			SI.put(sortingfolder.toString(), si);
			SO.put(sortingfolder.toString(), so);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						si.datasource();
						Path mergedfile = Sortutil.sortmerge(isasc, sortingfolder);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					SI.remove(sortingfolder.toString());
					SO.remove(sortingfolder.toString());
				}
				
			}).start();
		} else {
			//do nothing
		}
	}
}
