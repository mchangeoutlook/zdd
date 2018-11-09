package com.zdd.bdc.sort.distribute;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.sort.local.Sortoutput;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortdistribute{
	
	private BufferedReader mergedfilereader = null;
	private Vector<String> sortingservers = null;
	private Vector<Sortelement> distributearray = null;
	private Sortoutput output = null;
	private Long numofnotifies = 0l;
	private Path sortingfolder = null;
	private String ip = null;
	private int port = -1;
	private boolean isasc = true;
	
	public Sortdistribute(String theip, int theport, boolean asc, Vector<String> thesortingservers, Path thesortingfolder, Sortoutput theoutput) {
		sortingfolder = thesortingfolder;
		ip = theip;
		port = theport;
		isasc = asc;
		sortingservers = thesortingservers;
		distributearray = new Vector<Sortelement>(thesortingservers.size());
		output = theoutput;
	}

	public synchronized void change(long increaseordecrease) {
		Sorthouse.numofnotifies.put(sortingfolder.toString(), Sorthouse.numofnotifies.get(sortingfolder.toString())+increaseordecrease);
	}
	
	public synchronized void addtodistribute(String fromip, int fromport, String key, long amount) {
		
		this.notifyAll();
		
	}
	
	public void clear() {
		
	}
	
	public synchronized void startinathread() throws Exception {
		boolean done = false;
		while(!done) {
			if (numofnotifies==0) {
				wait();
			} else {
				//do nothing
			}
			change(-1);
			Sortelement minormax = Sortutil.findminmaxtodistribute(distributearray, isasc);
			if (minormax == null) {
				done = true;
			} else {
				if (ip.equals(minormax.ip())&&port==minormax.port()) {
					String keyamount = Sorthouse.next(sortingfolder);
					if (keyamount == null) {
						this.distributestatus = Sorthouse.DISTRIBUTE_ACCOMPLISHED;
						distributearray.set(distributearray.indexOf(minormax), null);
					} else {
						
					}
				} else {
					//do nothing
				}
				
				done = true;
				for (Sortelement se:distributearray) {
					if (se!=null) {
						done=false;
					}
				}
			}
		}
		
	}
	
}
