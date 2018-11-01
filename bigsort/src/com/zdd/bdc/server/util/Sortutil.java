package com.zdd.bdc.server.util;

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
import java.util.Vector;
import java.util.Map.Entry;
import com.zdd.bdc.client.util.CS;

public class Sortutil {

	private static Path sortingfilesfolder(boolean isasc, Path sortingfolder) {
		if (isasc) {
			return sortingfolder.resolve("goingbigger");
		} else {
			return sortingfolder.resolve("goingsmaller");
		}
	}

	private static Path sortmergefolder(boolean isasc, Path sortingfolder) {
		if (isasc) {
			return sortingfolder.resolve("gonebigger");
		} else {
			return sortingfolder.resolve("gonesmaller");
		}
	}

	public static void sortintofiles(boolean isasc, Map<String, Long> tosort, Path sortingfolder) throws Exception {
		try {
			Path sortingfilesfolder = sortingfilesfolder(isasc, sortingfolder);
			if (!Files.exists(sortingfilesfolder)) {
				Files.createDirectories(sortingfilesfolder);
			}
			Path sortintofile = sortingfilesfolder.resolve(String.valueOf(sortingfilesfolder.toFile().list().length));

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
		} catch(Exception e){
			clearfolder(sortingfilesfolder(isasc, sortingfolder));
			throw e;
		}finally {
			clearfolder(sortmergefolder(isasc, sortingfolder));
		}
	}

	private static void clearfolder(Path thefolder) {
		String[] files = thefolder.toFile().list();
		if (files != null) {
			for (String file : files) {
				try {
					Files.write(thefolder.resolve(file), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
					Files.deleteIfExists(thefolder.resolve(file));
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}

	public static Path sortmerge(boolean isasc, Path sortingfolder) throws Exception {
		Vector<BufferedReader> brs = null;
		try {
			Path sortmergefolder = sortmergefolder(isasc, sortingfolder);
			if (!Files.exists(sortmergefolder)) {
				Files.createDirectories(sortmergefolder);
			}
			Path mergedfile = sortmergefolder.resolve("done");
			String[] sortingfiles = sortingfilesfolder(isasc, sortingfolder).toFile().list();
			brs = new Vector<BufferedReader>(sortingfiles.length);
			Vector<Long> amounts = new Vector<Long>(sortingfiles.length);
			Vector<String> keys = new Vector<String>(sortingfiles.length);
			for (String sortingfile : sortingfiles) {
				BufferedReader br = Files.newBufferedReader(
						sortingfilesfolder(isasc, sortingfolder).resolve(sortingfile), Charset.forName("UTF-8"));
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
			clearfolder(sortmergefolder(isasc, sortingfolder));
			throw e;
		} finally {
			clearfolder(sortingfilesfolder(isasc, sortingfolder));
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

}
