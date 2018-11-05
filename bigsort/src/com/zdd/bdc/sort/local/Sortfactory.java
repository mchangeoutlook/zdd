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

	public synchronized static void start(Vector<String> sortingservers, Sortinput input, Sortoutput output,
			Sortstatus status) throws Exception {
		Path sortingfolder = input.sortingfolder();
		if (si.get(sortingfolder.toString()) == null) {
			Sortcontrol.to(sortingfolder, Sortcontrol.CONTINUE);
			si.put(sortingfolder.toString(), input);
			so.put(sortingfolder.toString(), output);
			ss.put(sortingfolder.toString(), status);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println(new Date() + " ==== started sorting local [" + sortingfolder + "]");
						input.datasource();
						Path mergedfile = Sortutil.sortmerge(input.isasc(), sortingfolder);
						br.put(sortingfolder.toString(), Files.newBufferedReader(mergedfile, Charset.forName("UTF-8")));

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
							int sortstatus = (Integer) Objectutil.convert(Theclient.request(iport[0],
									Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
							if (sortstatus == Sortstatus.MERGED || sortstatus == Sortstatus.SORTING) {
								validsortingservers.add(ipport);
							} else if (sortstatus == Sortstatus.NOTFOUND || sortstatus == Sortcontrol.CONTINUE) {
								// do nothing
							} else if (sortstatus == Sortcontrol.TERMINATE) {
								throw new Exception(
										"terminiated sorting : [" + sortingfolder + "] on [" + ipport + "]");
							} else {
								throw new Exception("error sort status: [" + sortstatus + "] of [" + sortingfolder
										+ "] from [" + ipport + "]");
							}
						}

						vs.put(sortingfolder.toString(), validsortingservers);

						// distribute the first data:
						for (String ipport : validsortingservers) {
							Map<String, Object> params = new Hashtable<String, Object>(6);
							params.put(CS.PARAM_KEY_KEY, sortingfolder);
							params.put(CS.PARAM_ACTION_KEY, CS.PARAM_ACTION_CREATE);
							params.put(CS.PARAM_DATA_KEY, Sortfactory.next(sortingfolder));
							String[] iport = CS.splitiport(ipport);
							int status = (Integer) Objectutil.convert(Theclient.request(iport[0],
									Integer.parseInt(iport[1]), Objectutil.convert(params), null, null));
							if (status == Sortcontrol.CONTINUE) {
								// do nothing
							} else if (status == Sortcontrol.TERMINATE) {
								throw new Exception(
										"terminiated sorting : [" + sortingfolder + "] on [" + ipport + "]");
							} else {
								throw new Exception("error distributing status: [" + status + "] of [" + sortingfolder
										+ "] from [" + ipport + "]");
							}
						}
					} catch (Exception e) {
						System.out.println(
								new Date() + " ==== error when distributing sort folder [" + sortingfolder + "]");
						e.printStackTrace();
						stop(sortingfolder, false);
					}
				}

			}).start();
		} else {
			// do nothing
		}
	}
}
