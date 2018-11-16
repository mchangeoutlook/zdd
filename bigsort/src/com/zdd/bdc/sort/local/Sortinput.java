package com.zdd.bdc.sort.local;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

import com.zdd.bdc.sort.util.Sortutil;

public abstract class Sortinput {
	
	private Map<String, Long> tosort = null;
	private boolean isasc = true;
	private Path sortingfolder = null;
	private int onefilecapacity = 10000;
	
	public final boolean isasc() {
		return isasc;
	}
	public final Path sortingfolder() {
		return sortingfolder;
	}
	public final int onefilecapacity() {
		return onefilecapacity;
	};
	
	public final void init() throws Exception {
		isasc = prepareisasc();
		sortingfolder = preparesortingfolder();
		onefilecapacity = prepareonefilecapacity();
		tosort = new Hashtable<String, Long>(onefilecapacity);
	}
	
	protected abstract boolean prepareisasc();
	
	protected abstract int prepareonefilecapacity();
	
	protected abstract Path preparesortingfolder() throws Exception;
	
	protected abstract void preparedatasource() throws Exception;
	
	protected final void input(String key, long value) throws Exception{
		tosort.put(key, value);
		if (tosort.size()==onefilecapacity) {
			Sortutil.sortintofiles(isasc, tosort, sortingfolder);
			tosort.clear();
		} else {
			//do nothing
		}
	}

	public final void inputmerge() throws Exception{
		if (!tosort.isEmpty()) {
			Sortutil.sortintofiles(isasc, tosort, sortingfolder);
			tosort.clear();
		} else {
			//do nothing
		}
		Sortutil.sortmerge(isasc, sortingfolder);
	}


}
