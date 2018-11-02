package com.zdd.bdc.client.sort;

import java.nio.file.Path;

public abstract class Sortinput {
	
	public boolean isasc() {
		return true;
	}
	
	public abstract Path sortingfolder() throws Exception;
	
	public abstract void datasource() throws Exception;
	
	protected void input(String key, long value, boolean isdone) throws Exception{
		
	}
	

}
