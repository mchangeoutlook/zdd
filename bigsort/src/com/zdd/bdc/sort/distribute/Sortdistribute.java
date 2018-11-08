package com.zdd.bdc.sort.distribute;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.sort.util.Sorthouse;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortdistribute{

	public static final int DISTRIBUTE_CONTINUE = 3;
	public static final int DISTRIBUTE_TERMINATE = 4;
	public static final int DISTRIBUTE_ACCOMPLISHED = 5;

	private BufferedReader mergedfilereader = null;
	private Vector<String> sortingservers = null;
	private Vector<Sortelement> distributearray = null;
	private Long numofnotifies = 0l;
	private int distributestatus = DISTRIBUTE_CONTINUE;
	private Path sortingfolder = null;
	private String ip = null;
	private int port = -1;
	private boolean isasc = true;
	
	public Sortdistribute(String theip, int theport, boolean asc, Vector<String> thesortingservers, Path thesortingfolder) {
		sortingfolder = thesortingfolder;
		ip = theip;
		port = theport;
		isasc = asc;
		sortingservers = thesortingservers;
		if (sortingservers!=null&&!sortingservers.isEmpty()) {
			distributearray = new Vector<Sortelement>(thesortingservers.size());
		} else {
			distributestatus = DISTRIBUTE_TERMINATE;
		}
	}
	
	public int status() {
		return distributestatus;
	}

	public synchronized void change(long increaseordecrease) {
		Sorthouse.numofnotifies.put(sortingfolder.toString(), Sorthouse.numofnotifies.get(sortingfolder.toString())+increaseordecrease);
	}
	
	public synchronized void addtodistribute(String fromip, int fromport, String key, long amount) {
		
		if () {
			
		}
		
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
