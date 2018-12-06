package com.zdd.bdc.sort.local;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.zdd.bdc.client.ex.Theclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.sort.distribute.Sortdistribute;
import com.zdd.bdc.sort.util.Sortstatus;
import com.zdd.bdc.sort.util.Sortutil;

public class Sortfactory {

	public static Map<String, Sortdistribute> sortdistributes = new Hashtable<String, Sortdistribute>();
	
	public static void clear(Path sortingfolder, String status) {
		try {
			Sortstatus.set(sortingfolder, status);
		} catch (Exception e) {
			// do nothing
		}

		try {
			sortdistributes.remove(sortingfolder.toString()).clear();
		} catch (Exception e) {
			// do nothing
		}

		try {
			Sortutil.clear(sortingfolder, status);
		} catch (Exception e) {
			// do nothing
		}
	}

	public synchronized static void start(String ip, int port, Collection<String> sortingservers, Sortinput input,
			Sortoutput output) {
		try {
			input.init();
		} catch (Exception e) {
			System.out.println(new Date() + " ==== error init [" + input.getClass() + "]");
			e.printStackTrace();
			return;
		}
		Path sortingfolder = input.sortingfolder();
		if (Sortstatus.get(sortingfolder) == null || Sortstatus.get(sortingfolder) == Sortstatus.ACCOMPLISHED
				|| Sortstatus.get(sortingfolder) == Sortstatus.TERMINATE
				|| Sortstatus.get(sortingfolder) == Sortstatus.SORT_NOTINCLUDED) {
			try {
				Sortstatus.set(sortingfolder, Sortstatus.SORT_INCLUDED);
			} catch (Exception e) {
				System.out.println(
						new Date() + " ==== error set [" + sortingfolder + "] status to " + Sortstatus.SORT_INCLUDED);
				e.printStackTrace();
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println(new Date() + " ==== started sorting local [" + sortingfolder + "]");
						input.preparedatasource();
						input.inputmerge();
						System.out.println(new Date() + " ==== done sorting local [" + sortingfolder + "]");

						sortdistributes.put(sortingfolder.toString(), new Sortdistribute(ip, port, input.isasc(),
								sortingservers.size(), sortingfolder, output));

						Sortstatus.set(sortingfolder, Sortstatus.READY_TO_DISTRIBUTE);

						System.out.println(new Date() + " ==== started checking included sorting servers for ["+sortingfolder+"]");
						Vector<String> validsortingservers = new Vector<String>(sortingservers.size());
						boolean waitingforlocalsortdone = true;
						while (!Sortstatus.TERMINATE.equals(Sortstatus.get(sortingfolder)) && waitingforlocalsortdone) {
							waitingforlocalsortdone = false;
							validsortingservers.clear();
							for (String ipport : sortingservers) {
								Map<String, Object> params = new Hashtable<String, Object>(6);
								params.put(STATIC.PARAM_KEY_KEY, sortingfolder.toString());
								params.put(STATIC.PARAM_ACTION_KEY, STATIC.PARAM_ACTION_READ);
								String[] iport = STATIC.splitiport(ipport);
								String check = (String) Objectutil.convert(Theclient.request(iport[0],
										Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
								if (Sortstatus.READY_TO_DISTRIBUTE.equals(check)) {
									validsortingservers.add(ipport);
								} else if (Sortstatus.SORT_NOTINCLUDED.equals(check)) {
									// do nothing
								} else if (Sortstatus.SORT_INCLUDED.equals(check)) {
									System.out.println(new Date() + " ==== waiting for local sorting ["+sortingfolder+"] done on [" + ipport
											+ "], recheck in 30 seconds");
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
										sortdistributes.get(sortingfolder.toString()).startinathread(validsortingservers);
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
						System.out
								.println(new Date() + " ==== error starting big sort of local [" + sortingfolder + "]");
						e.printStackTrace();
						Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
					}

				}
			}).start();
		} else{
			System.out
			.println(new Date() + " ==== ignore sorting [" + sortingfolder + "] due to sort status ["+Sortstatus.get(sortingfolder)+"]");
		}
	}
}