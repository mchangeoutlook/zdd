package com.zdd.bdc.sort.util;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.server.util.SS;
import com.zdd.bdc.sort.local.Sortoutput;
import com.zdd.bdc.sort.local.Sortstatus;

public class Sorthouse {

	public static Map<String, Sortoutput> sortoutputs = new Hashtable<String, Sortoutput>();
	public static Map<String, Sortstatus> sortstatuses = new Hashtable<String, Sortstatus>();
	
	public static Map<String, BufferedReader> mergedfilereaders = new Hashtable<String, BufferedReader>();
	
	public static Map<String, Vector<String>> validsortingservers = new Hashtable<String, Vector<String>>();
	
	public static Map<String, Vector<Map<String, String>>> distributearrays = new Hashtable<String, Vector<Map<String, String>>>();
	
	public static Map<String, Integer> numofnotifies = new Hashtable<String, Integer>();

	public static final int DISTRIBUTE_CONTINUE = 3;
	public static final int DISTRIBUTE_TERMINATE = 4;
	public static final int DISTRIBUTE_ACCOMPLISHED = 5;
	
	private static Map<String, Integer> SC = new Hashtable<String, Integer>();
	
	public static void control(Path sortingfolder, int status) {
		SC.put(sortingfolder.toString(), status);
	}
	
	public static int control(Path sortingfolder) {
		return SC.get(sortingfolder.toString());
	}
	
	public static synchronized void stop(Path sortingfolder, boolean issuccessful) {
		so.remove(sortingfolder.toString());
		ss.remove(sortingfolder.toString());
		vs.remove(sortingfolder.toString());
		try {
			br.get(sortingfolder.toString()).close();
		} catch (Exception e) {
			// do nothing
		}
		br.remove(sortingfolder.toString());
		if (issuccessful) {
			System.out.println(new Date() + " ==== Accomplished distributing sort folder [" + sortingfolder + "]");
		} else {
			Sortcontrol.to(sortingfolder, Sortcontrol.TERMINATE);
			System.out.println(new Date() + " ==== Terminated distributing sort folder [" + sortingfolder + "]");
		}
	}
	

	public static String next(Path sortingfolder) throws Exception {
		synchronized (SS.syncfile(sortingfolder)) {
			String line = br.get(sortingfolder.toString()).readLine();
			if (line == null) {
				stop(sortingfolder, true);
			} else {
				// do nothing
			}
			return line;
		}
	}
	
	public static void put(Path sortingfolder, String ipport, String keyamount) throws Exception {
		synchronized (SS.syncfile(sortingfolder)) {
			if (da.get(sortingfolder.toString())==null) {
				da.put(sortingfolder.toString(), new Vector<Map<String, String>>(vs.get(sortingfolder.toString()).size()));
			} else {
				//do nothing
			}
			Map<String, String> v = new Hashtable<String, String>();
			v.put("ipport", ipport);
			v.put("keyamount", keyamount);
			da.get(sortingfolder.toString()).add(v);
			if (da.get(sortingfolder.toString()).size()==vs.get(sortingfolder.toString()).size()) {
				//Sortdistrubute.
			}
		}
	}
}
