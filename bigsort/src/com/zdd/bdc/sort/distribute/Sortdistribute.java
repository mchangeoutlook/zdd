package com.zdd.bdc.sort.distribute;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.sort.local.Sortoutput;
import com.zdd.bdc.sort.util.Sortstatus;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortdistribute {

	private BufferedReader mergedfilereader = null;
	private Vector<String> sortingservers = null;
	private Map<String, Sortelement> distributearray = null;
	private Sortoutput output = null;
	private boolean stop = false;
	private Long numofnotifies = 0l;
	private Path sortingfolder = null;
	private String ip = null;
	private int port = -1;
	private boolean isasc = true;
	private long position = 0;

	public Sortdistribute(String theip, int theport, boolean asc, int initdistributearraycapacity,
			Path thesortingfolder, Sortoutput theoutput) throws Exception {
		sortingfolder = thesortingfolder;
		ip = theip;
		port = theport;
		isasc = asc;
		output = theoutput;
		output.init(sortingfolder);
		distributearray = new HashMap<String, Sortelement>(initdistributearraycapacity);
	}

	public synchronized void addtodistribute(String fromip, int fromport, String key, long amount) {
		if (key == null) {
			distributearray.put(STATIC.splitiport(fromip, String.valueOf(fromport)), null);
		} else {
			position++;
			distributearray.put(STATIC.splitiport(fromip, String.valueOf(fromport)),
					new Sortelement(fromip, fromport, key, amount));
		}
		clearnumofnotifies(false);

		notifyAll();
	}

	public synchronized void clearnumofnotifies(boolean clear) {
		if (clear) {
			numofnotifies = 0l;
		} else {
			numofnotifies++;
		}
	}

	public void clear() {
		stop = true;
	}

	private void addtoalldistribute(String keyamount) throws Exception {
		for (String ipport : sortingservers) {
			if (!stop) {
				if (STATIC.splitiport(ip, String.valueOf(port)).equals(ipport)) {
					if (keyamount == null) {
						addtodistribute(ip, port, null, -1);
					} else {
						String[] ka = STATIC.splitenc(keyamount);
						addtodistribute(ip, port, ka[0], Long.parseLong(ka[1]));
					}
				} else {
					Map<String, Object> params = new Hashtable<String, Object>(6);
					params.put(STATIC.PARAM_KEY_KEY, sortingfolder.toString());
					params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_CREATE);
					if (keyamount!=null) {
						params.put(STATIC.PARAM_DATA_KEY, keyamount);
					} else {
						//do nothing
					}
					params.put(STATIC.PARAM_INDEX_KEY, STATIC.splitiport(ip, String.valueOf(port)));
					String[] iport = STATIC.splitiport(ipport);
					String res = (String) Objectutil.convert(Theclient.request(iport[0], Integer.parseInt(iport[1]),
							Objectutil.convert(params), null, null));
					if (Sortstatus.READY_TO_DISTRIBUTE.equals(res) || Sortstatus.ACCOMPLISHED.equals(res)) {
						// do nothing
					} else {
						throw new Exception(
								"error distribute: [" + res + "] of [" + sortingfolder + "] to [" + ipport + "]");
					}
				}
			} else {
				// do nothing
			}
		}
	}

	public synchronized void startinathread(Vector<String> thesortingservers) throws Exception {
		try {
			sortingservers = thesortingservers;
			mergedfilereader = Files.newBufferedReader(
					STATIC.LOCAL_DATAFOLDER.getParent().resolve(sortingfolder).resolve(Sortutil.mergedfilename),
					Charset.forName("UTF-8"));
			String keyamount = mergedfilereader.readLine();
			addtoalldistribute(keyamount);
			
			System.out.println(new Date() + " ==== starting to distribute sort folder ["
					+ sortingfolder + "] among ["+sortingservers+"]");
			
			while (!Sortstatus.TERMINATE.equals(Sortstatus.get(sortingfolder)) && !stop) {
				if (numofnotifies == 0 || distributearray.size() != sortingservers.size()) {
					wait(30000);
				} else {
					clearnumofnotifies(true);
					Sortelement se = Sortutil.findminmax(
							distributearray.values(),
							isasc);
					if (se.ip().equals(ip) && se.port() == port) {
						output.output(position - sortingservers.size(), se.key(), se.amount());
						keyamount = mergedfilereader.readLine();
						if (keyamount == null) {
							addtoalldistribute(null);
							break;
						} else {
							addtoalldistribute(keyamount);
						}
					} else {
						// do nothing
					}
				}
			}
			if (keyamount == null) {
				Sortutil.clear(sortingfolder, Sortstatus.ACCOMPLISHED);
				Sortstatus.set(sortingfolder, Sortstatus.ACCOMPLISHED);
			} else {
				// do nothing
			}
			
		} finally {
			distributearray.clear();
			sortingservers.clear();
			try {
				mergedfilereader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

}
