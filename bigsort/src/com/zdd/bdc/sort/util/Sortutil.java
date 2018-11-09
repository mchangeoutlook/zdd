package com.zdd.bdc.sort.util;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.sort.distribute.Sortelement;

public class Sortutil {

	private static final String mergedfilename = "done";

	public static void sortintofiles(boolean isasc, Map<String, Long> tosort, Path sortingfolder) throws Exception {
		try {
			if (!Files.exists(sortingfolder)) {
				Files.createDirectories(sortingfolder);
			} else {
				if (Files.exists(sortingfolder.resolve(mergedfilename))) {
					clearfolder(sortingfolder, null);
				} else {
					// do nothing
				}
			}
			Path sortintofile = sortingfolder.resolve(String.valueOf(sortingfolder.toFile().list().length));

			List<Map.Entry<String, Long>> entryList = new ArrayList<Map.Entry<String, Long>>(tosort.entrySet());
			Collections.sort(entryList, new Comparator<Entry<String, Long>>() {

				@Override
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					Entry<String, Long> me1 = (Entry<String, Long>) o1;
					Entry<String, Long> me2 = (Entry<String, Long>) o2;
					if (isasc) {
						return me1.getValue().compareTo(me2.getValue());
					} else {
						return me2.getValue().compareTo(me1.getValue());
					}
				}

			});

			Iterator<Map.Entry<String, Long>> iter = entryList.iterator();
			Map.Entry<String, Long> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				Files.write(sortintofile,
						CS.tobytes(CS.splitenc(tmpEntry.getKey(), String.valueOf(tmpEntry.getValue()))
								+ System.lineSeparator()),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
			}
		} catch (Exception e) {
			clearfolder(sortingfolder, null);
			throw e;
		}
	}

	public static void clearfolder(Path thefolder, String except) throws Exception {
		String[] files = thefolder.toFile().list();
		if (files != null) {
			for (String file : files) {
				if (!file.equals(except)) {
					Files.write(thefolder.resolve(file), new byte[0], StandardOpenOption.CREATE,
							StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
					Files.deleteIfExists(thefolder.resolve(file));
				}
			}
		}
	}

	public static Path sortmerge(boolean isasc, Path sortingfolder) throws Exception {
		Vector<BufferedReader> brs = null;
		try {
			Path mergedfile = sortingfolder.resolve(mergedfilename);
			String[] sortingfiles = sortingfolder.toFile().list();
			brs = new Vector<BufferedReader>(sortingfiles.length);
			Vector<Long> amounts = new Vector<Long>(sortingfiles.length);
			Vector<String> keys = new Vector<String>(sortingfiles.length);
			for (String sortingfile : sortingfiles) {
				BufferedReader br = Files.newBufferedReader(sortingfolder.resolve(sortingfile),
						Charset.forName("UTF-8"));
				brs.add(br);
				String line = br.readLine();
				String[] keyamount = CS.splitenc(line);
				String key = keyamount[0];
				Long amount = Long.parseLong(keyamount[1]);
				amounts.add(amount);
				keys.add(key);
			}

			while (true) {
				Long min = null;
				int minindex = 0;
				for (int i = 0; i < amounts.size(); i++) {
					if (amounts.get(i) == null) {
						continue;
					}
					if (min == null) {
						min = amounts.get(i);
						minindex = i;
					} else {
						if (isasc) {
							if (min > amounts.get(i)) {
								min = amounts.get(i);
								minindex = i;
							}
						} else {
							if (min < amounts.get(i)) {
								min = amounts.get(i);
								minindex = i;
							}
						}
					}
				}
				if (min == null) {
					break;
				}
				String line = null;
				Files.write(mergedfile,
						CS.tobytes(CS.splitenc(keys.get(minindex), String.valueOf(amounts.get(minindex)))
								+ System.lineSeparator()),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
				line = brs.get(minindex).readLine();
				if (line != null) {
					String[] keyamount = CS.splitenc(line);
					String key = keyamount[0];
					Long amount = Long.parseLong(keyamount[1]);
					amounts.set(minindex, amount);
					keys.set(minindex, key);
				} else {
					amounts.set(minindex, null);
					keys.set(minindex, null);
				}
			}
			return mergedfile;
		} catch (Exception e) {
			clearfolder(sortingfolder, null);
			throw e;
		} finally {
			clearfolder(sortingfolder, mergedfilename);
			if (brs != null) {
				for (BufferedReader br : brs) {
					try {
						br.close();
					} catch (Exception e) {
						// do nothing
					}
				}
			}
		}
	}

	public static Sortelement findminmax(Vector<Sortelement> distributearray, boolean isasc) {
		Sortelement returnvalue = null;
		for (Sortelement t : distributearray) {
			if (returnvalue == null) {
				if (t != null) {
					returnvalue = t;
				} else {
					// do nothing
				}
			} else {
				if (t != null) {
					if (isasc) {
						if (t.amount() < returnvalue.amount()) {
							returnvalue = t;
						} else {
							// do nothing
						}
					} else {
						if (t.amount() > returnvalue.amount()) {
							returnvalue = t;
						} else {
							// do nothing
						}
					}
				} else {
					// do nothing
				}
			}
		}
		return returnvalue;
	}
}
