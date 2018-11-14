package com.zdd.bdc.sort.local;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.sort.util.Sortutil;

public abstract class Sortinput {
	
	private Map<String, Long> tosort = null;
	public boolean isasc = true;
	public Path sortingfolder = null;
	public int onefilecapacity = 10000;
	public void init() throws Exception {
		isasc = isasc();
		sortingfolder = sortingfolder();
		onefilecapacity = onefilecapacity();
		tosort = new Hashtable<String, Long>(onefilecapacity);
	}
	
	protected abstract boolean isasc();
	
	protected abstract int onefilecapacity();
	
	protected abstract Path sortingfolder() throws Exception;
	
	protected abstract void datasource() throws Exception;
	
	protected void input(String key, long value, boolean isdone) throws Exception{
		tosort.put(key, value);
		if (tosort.size()==onefilecapacity||isdone) {
			Sortutil.sortintofiles(isasc, tosort, sortingfolder);
			tosort.clear();
		} else {
			//do nothing
		}
		if (isdone) {
			Sortutil.sortmerge(isasc, sortingfolder);
		} else {
			//do nothing
		}
	}
	

}
