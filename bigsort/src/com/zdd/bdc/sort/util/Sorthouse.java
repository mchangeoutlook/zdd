package com.zdd.bdc.sort.util;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.sort.distribute.Sortcheck;
import com.zdd.bdc.sort.distribute.Sortdistribute;
import com.zdd.bdc.sort.local.Sortoutput;

public class Sorthouse {
	public static Map<Path, Sortoutput> sortoutputs = new Hashtable<Path, Sortoutput>();
	public static Map<Path, Sortcheck> sortchecks = new Hashtable<Path, Sortcheck>();
	public static Map<Path, Sortdistribute> sortdistributes = new Hashtable<Path, Sortdistribute>();
}
