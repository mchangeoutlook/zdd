package com.zdd.bdc.sort.distribute;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.sort.local.Sortfactory;

public class Sortserver implements Theserverprocess {

	public static final String sortcheckclasskey = "sortcheckclasskey";

	private String ip = null;
	private int port = -1;
	private static Sortcheck check = null;

	@Override
	public void init(String serverlocalip, int serverlocalport, int bigfilehash,
			Map<String, Object> additionalserverconfig) throws Exception {
		ip = serverlocalip;
		port = serverlocalport;
		check = (Sortcheck) Class.forName(additionalserverconfig.get(sortcheckclasskey).toString())
				.getDeclaredConstructor().newInstance();
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] request(byte[] param) throws Exception {
		Map<String, Object> params = (Map<String, Object>) Objectutil.convert(param);
		Path sortingfolder = (Path) params.get(CS.PARAM_KEY_KEY);
		if (Sortfactory.sortdistributes.get(sortingfolder) != null && Sortdistribute.DISTRIBUTE_TERMINATE
				.equals(Sortfactory.sortdistributes.get(sortingfolder).status())) {
			return Objectutil.convert(Sortdistribute.DISTRIBUTE_TERMINATE);
		} else {
			if (CS.PARAM_ACTION_READ.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
				String c = null;
				try {
					c = check.check(sortingfolder);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== error when checking sort folder [" + sortingfolder + "] on ["
							+ CS.splitiport(ip, String.valueOf(port)) + "]");
					e.printStackTrace();
					throw e;
				}
				if (Sortcheck.SORT_INCLUDED.equals(c)) {
					if (Sortfactory.sortdistributes.get(sortingfolder) != null) {
						return Objectutil.convert(Sortcheck.SORT_MERGED);
					} else {
						return Objectutil.convert(c);
					}
				} else {
					return Objectutil.convert(c);
				}
			} else if (CS.PARAM_ACTION_CREATE.equals(params.get(CS.PARAM_ACTION_KEY).toString())) {
				try {
					String[] keyamount = CS.splitenc(params.get(CS.PARAM_DATA_KEY).toString());
					String[] ipport = CS.splitiport(params.get(CS.PARAM_INDEX_KEY).toString());
					Sortfactory.sortdistributes.get(sortingfolder).addtodistribute(ipport[0],
							Integer.parseInt(ipport[1]), keyamount[0], Long.parseLong(keyamount[1]));
					return Objectutil.convert(Sortdistribute.DISTRIBUTE_CONTINUE);
				} catch (Exception e) {
					System.out.println(new Date() + " ==== error when distributing sort folder [" + sortingfolder
							+ "] on [" + CS.splitiport(ip, String.valueOf(port)) + "]");
					e.printStackTrace();
					Sortfactory.sortdistributes.get(sortingfolder).status(Sortdistribute.DISTRIBUTE_TERMINATE);
					return Objectutil.convert(Sortdistribute.DISTRIBUTE_TERMINATE);
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public Inputprocess requestinput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream requestoutput(byte[] param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
