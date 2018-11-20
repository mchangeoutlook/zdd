package com.zdd.bdc.sort.local;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.sort.distribute.Sortdistribute;
import com.zdd.bdc.sort.util.Sortstatus;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortfactory {

	public static Map<Path, Sortdistribute> sortdistributes = new Hashtable<Path, Sortdistribute>();
	public static Map<Path, Thread> sortings = new Hashtable<Path, Thread>();

	public static void clear(Path sortingfolder, String status) {
		try {
			Sortstatus.set(sortingfolder, status);
		} catch (Exception e) {
			// do nothing
		}

		sortings.remove(sortingfolder);

		try {
			sortdistributes.remove(sortingfolder).clear();
		} catch (Exception e) {
			// do nothing
		}

		try {
			Sortutil.clear(sortingfolder, status);
		} catch (Exception e) {
			// do nothing
		}
		
		System.out.println(new Date() + " ==== terminiated sorting [" + sortingfolder + "]");

	}

	public synchronized static void start(String ip, int port, Collection<String> sortingservers, Sortinput input,
			Sortoutput output) {
		try {
			input.init();
		}catch(Exception e) {
			System.out.println(new Date() + " ==== error init [" + input.getClass() + "]");
			e.printStackTrace();
			return;
		}
		Path sortingfolder = input.sortingfolder();
		if (sortings.get(sortingfolder) == null&&Sortstatus.get(sortingfolder)==null) {
			try {
				Sortstatus.set(sortingfolder, Sortstatus.SORT_INCLUDED);
			}catch(Exception e) {
				System.out.println(new Date() + " ==== error set [" + sortingfolder + "] status to "+Sortstatus.SORT_INCLUDED);
				e.printStackTrace();
				return;
			}
			sortings.put(sortingfolder, new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println(new Date() + " ==== started sorting local [" + sortingfolder + "]");
						input.preparedatasource();
						input.inputmerge();
						System.out.println(new Date() + " ==== done sorting local [" + sortingfolder + "]");

						sortdistributes.put(sortingfolder,
								new Sortdistribute(ip, port, input.isasc(), sortingservers.size(), sortingfolder, output));
						
						Sortstatus.set(sortingfolder, Sortstatus.READY_TO_DISTRIBUTE);
						
						System.out.println(new Date() + " ==== started checking included sorting servers");
						Vector<String> validsortingservers = new Vector<String>(sortingservers.size());
						boolean waitingforlocalsortdone = true;
						while(!Sortstatus.TERMINATE.equals(Sortstatus.get(sortingfolder))&&waitingforlocalsortdone) {
							waitingforlocalsortdone = false;
							validsortingservers.clear();
							for (String ipport : sortingservers) {
								Map<String, Object> params = new Hashtable<String, Object>(6);
								params.put(CS.PARAM_KEY_KEY, sortingfolder);
								params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_READ);
								String[] iport = CS.splitiport(ipport);
								String check = (String) Objectutil.convert(Theclient.request(iport[0],
										Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
								if (Sortstatus.READY_TO_DISTRIBUTE.equals(check)) {
									validsortingservers.add(ipport);
								} else if (Sortstatus.SORT_NOTINCLUDED.equals(check)) {
									// do nothing
								} else if (Sortstatus.SORT_INCLUDED.equals(check)) {
									System.out.println(new Date()+" ==== waiting for local sorting done on [" + ipport + "], recheck in 30 seconds");
									Thread.sleep(30000);
									waitingforlocalsortdone = true;
									break;
								} else {
									throw new Exception("error sort check: [" + check + "] of [" + sortingfolder
											+ "] from [" + ipport + "]");
								}
							}
						}
						if (validsortingservers.isEmpty()) {
							throw new Exception("no sorting server");
						} else {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										sortdistributes.get(sortingfolder).startinathread(validsortingservers);
	
										System.out.println(new Date() + " ==== started distributing sort folder ["
												+ sortingfolder + "]");
									} catch (Exception e) {
										System.out.println(
												new Date() + " ==== error when starting to distribute sort folder ["
														+ sortingfolder + "]");
										e.printStackTrace();
										Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
									}
								}
	
							}).start();
						}
					} catch (Exception e) {
						System.out.println(new Date() + " ==== error starting big sort of local [" + sortingfolder + "]");
						e.printStackTrace();
						Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
					}

				}
			}));

			try {
				sortings.get(sortingfolder).start();
			}catch(Exception e) {
				System.out.println(new Date() + " ==== error starting big sort thread of local [" + sortingfolder + "]");
				e.printStackTrace();
				Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
			}

		}
	}
}
