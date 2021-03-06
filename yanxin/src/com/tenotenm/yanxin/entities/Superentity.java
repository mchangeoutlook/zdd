package com.tenotenm.yanxin.entities;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Bigclient;
import com.zdd.bdc.client.biz.Dataclient;
import com.zdd.bdc.client.biz.Uniqueindexclient;

public abstract class Superentity {
	
	public abstract void setKey(String key);
	public abstract String getKey();
	
	public String filter() {
		return this.getClass().getSimpleName()+"-ukey";
	}
	
	public void create(String key) throws Exception {
		if (key==null) {
			if (getKey()==null) {
				key = Bigclient.newbigdatakey();
				setKey(key);
			} else {
				key = getKey();
			}
		}
		String tablename =  this.getClass().getSimpleName();
		Dataclient dc = Dataclient.getinstance(Reuse.namespace_yanxin, tablename).key(key);
		Method[] M = this.getClass().getDeclaredMethods();
		for (Method m:M) {
			if (m.getName().startsWith("add4create_")) {
				Object[] o = (Object[]) m.invoke(this);
				if (o!=null) {
					dc.add4create(m.getName().substring(11), o[0].toString(), Integer.parseInt(o[1].toString()));
				}
			}
		}
		dc.create(Reuse.app_data);
		setKey(key);
	}
	
	public void createunique(String key, String uniqueindex) throws Exception {
		if (key==null) {
			if (getKey()==null) {
				key = Bigclient.newbigdatakey();
				setKey(key);
			} else {
				key = getKey();
			}
		}
		Uniqueindexclient.getinstance(Reuse.namespace_yanxin, uniqueindex).createunique(filter(), key);
		create(key);
	}

	public void createpaged(String key, String pagedindex, boolean createdata) throws Exception {
		createpaged(0, key, pagedindex, createdata);
	}

	public void modifyunique(String newkey, String index) throws Exception {
		Uniqueindexclient.getinstance(Reuse.namespace_yanxin, index).modifyunique(filter(), newkey);
	}
	
	public void createpaged(long itemseq, String key, String pagedindex,  boolean createdata) throws Exception {
		if (key==null) {
			if (getKey()==null) {
				key = Bigclient.newbigdatakey();
				setKey(key);
			} else {
				key = getKey();
			}
		}
		Uniqueindexclient.getinstance(Reuse.namespace_yanxin, pagedindex+"-"+itemseq).createunique(filter(), key);
		if (createdata) {
			create(key);
		}
	}

	public void modify(String key) throws Exception {
		if (key==null&&getKey()!=null) {
			key = getKey();
		}
		String tablename =  this.getClass().getSimpleName();
		Dataclient dc = Dataclient.getinstance(Reuse.namespace_yanxin, tablename).key(key);
		Method[] M = this.getClass().getDeclaredMethods();
		for (Method m:M) {
			if (m.getName().startsWith("add4modify_")) {
				Object o = m.invoke(this);
				if (o!=null) {
					dc.add4modify(m.getName().substring(11), o.toString());
				}
			}
		}
		dc.modify(Reuse.app_data);
	}

	public void increment(String key) throws Exception {
		if (key==null&&getKey()!=null) {
			key = getKey();
		}
		String tablename =  this.getClass().getSimpleName();
		Dataclient dc = Dataclient.getinstance(Reuse.namespace_yanxin, tablename).key(key);
		Method[] M = this.getClass().getDeclaredMethods();
		Vector<String> read_4increments = new Vector<String>(M.length);
		Date today = new Date();
		for (Method m:M) {
			if (m.getName().startsWith("add4increment_")) {
				Object o = m.invoke(this);
				if (o!=null) {
					dc.add4increment(m.getName().substring(14), Long.parseLong(o.toString()));
					read_4increments.add("read_"+m.getName().substring(14));
					String attr = m.getName().substring(m.getName().indexOf("_")+1);
					Method toclear = this.getClass().getDeclaredMethod("set"+attr.substring(0,1).toUpperCase()+attr.substring(1)+"4increment", Long.class);
					toclear.invoke(this, 0l);
				}
			}
			if (m.getName().startsWith("add4incrementtoday_")) {
				Object o = m.invoke(this);
				if (o!=null) {
					dc.add4increment(m.getName().substring(19)+"_"+Reuse.yyyyMMdd(today), Long.parseLong(o.toString()));
					read_4increments.add("readtoday_"+m.getName().substring(19));
					String attr = m.getName().substring(m.getName().indexOf("_")+1);
					Method toclear = this.getClass().getDeclaredMethod("set"+attr.substring(0,1).toUpperCase()+attr.substring(1)+"4increment", Long.class);
					toclear.invoke(this, 0l);
				}
			}
		}
		Map<String, Long> data = dc.increment(Reuse.app_data);
		for (Method m:M) {
			if (read_4increments.contains(m.getName())) {
				if (m.getName().startsWith("read_")) {
					if (data.get(m.getName().substring(5))!=null) {
						m.invoke(this, String.valueOf(data.get(m.getName().substring(5))));
					}
				}
				if (m.getName().startsWith("readtoday_")) {
					if (data.get(m.getName().substring(10)+"_"+Reuse.yyyyMMdd(today))!=null) {
						m.invoke(this, String.valueOf(data.get(m.getName().substring(10)+"_"+Reuse.yyyyMMdd(today))));
					}
				}
			}
		}
	}

	public void read(String key) throws Exception {
		if (key==null&&getKey()!=null) {
			key = getKey();
		}
		String tablename =  this.getClass().getSimpleName();
		Dataclient dc = Dataclient.getinstance(Reuse.namespace_yanxin, tablename).key(key);
		Method[] M = this.getClass().getDeclaredMethods();
		Date today = new Date();
		for (Method m:M) {
			if (m.getName().startsWith("read_")) {
				dc.add(m.getName().substring(5));
			}
			if (m.getName().startsWith("readtoday_")) {
				dc.add(m.getName().substring(10)+"_"+Reuse.yyyyMMdd(today));
			}
		}
		Map<String, String> data = dc.read(Reuse.app_data);
		if (data==null||data.isEmpty()) {
			throw new Exception(Reuse.NOTFOUND);
		}
		for (Method m:M) {
			if (m.getName().startsWith("read_")) {
				if (data.get(m.getName().substring(5))!=null) {
					m.invoke(this, data.get(m.getName().substring(5)));
				}
			}
			if (m.getName().startsWith("readtoday_")) {
				if (data.get(m.getName().substring(10)+"_"+Reuse.yyyyMMdd(today))!=null) {
					m.invoke(this, data.get(m.getName().substring(10)+"_"+Reuse.yyyyMMdd(today)));
				}
			}
		}
		setKey(key);
	}
	
	public void readunique(String uniqueindex) throws Exception {
		String key = Uniqueindexclient.getinstance(Reuse.namespace_yanxin, uniqueindex).readunique(filter());
		if (key==null||key.trim().isEmpty()) {
			throw new Exception(Reuse.NOTFOUND);
		}
		read(key);
	}

	public String readpaged(String pagedindex) throws Exception {
		return Uniqueindexclient.getinstance(Reuse.namespace_yanxin, pagedindex+"-0").readunique(filter());
	}

	public Vector<String> readpaged(String pagedindex, long pagenum) throws Exception {
		Vector<String> ret = new Vector<String>();
		long onepageitems = Reuse.getlongvalueconfig("onepage.items");
		long start = pagenum*onepageitems;
		for (long i=start;i<start+onepageitems;i++) {
			String r = Uniqueindexclient.getinstance(Reuse.namespace_yanxin, pagedindex+"-"+i).readunique(filter());
			if (r!=null&&!r.trim().isEmpty()) {
				ret.add(r);
			} else {
				break;
			}
		}
		return ret;
	}

	protected int calcextravaluecapcity(String initvalue, int limitsize) {
		try{
			byte[] b = initvalue.getBytes("UTF-8");
			if (b.length>=limitsize) {
				return 0;
			} else {
				return limitsize-b.length;
			}
		}catch(Exception e) {
			return limitsize;
		}
	}
}
