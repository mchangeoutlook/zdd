package com.zdd.bdc.sort.distribute;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.server.ex.Inputprocess;
import com.zdd.bdc.server.ex.Theserverprocess;
import com.zdd.bdc.sort.local.Sortfactory;
import com.zdd.bdc.sort.util.Sortstatus;

public class Sortserver implements Theserverprocess {

	public static final String sortcheckclasskey = "sortcheckclasskey";

	private String ip = null;
	private int port = -1;
	private Sortcheck check = null;

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
		Path sortingfolder = Paths.get((String) params.get(STATIC.PARAM_KEY_KEY));
		if (Sortstatus.TERMINATE.equals(Sortstatus.get(sortingfolder))) {
			return Objectutil.convert(Sortstatus.TERMINATE);
		} else {
			Map<String, String> additionalconfigs  =(Map<String, String>)params.get(STATIC.PARAM_DATA_KEY);
			if (STATIC.PARAM_ACTION_READ.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
				if (Sortstatus.get(sortingfolder)==null) {
					try {
						Sortstatus.set(sortingfolder, check.check(sortingfolder,additionalconfigs));
					} catch (Exception e) {
						System.out.println(new Date() + " ==== error when checking sort folder [" + sortingfolder + "] on ["
								+ STATIC.splitiport(ip, String.valueOf(port)) + "]");
						e.printStackTrace();
						Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
					}
				} else {
					//do nothing
				}
				return Objectutil.convert(Sortstatus.get(sortingfolder));
			} else if (STATIC.PARAM_ACTION_CREATE.equals(params.get(STATIC.PARAM_ACTION_KEY).toString())) {
				try {
					String[] ipport = STATIC.splitiport(params.get(STATIC.PARAM_INDEX_KEY).toString());
					if (params.get(STATIC.PARAM_DATA_KEY)==null) {
						Sortfactory.sortdistributes.get(sortingfolder).addtodistribute(ipport[0],
								Integer.parseInt(ipport[1]), null, -1);
					} else {
						String[] keyamount = STATIC.splitenc(params.get(STATIC.PARAM_DATA_KEY).toString());
						Sortfactory.sortdistributes.get(sortingfolder).addtodistribute(ipport[0],
								Integer.parseInt(ipport[1]), keyamount[0], Long.parseLong(keyamount[1]));
					}
				} catch (Exception e) {
					System.out.println(new Date() + " ==== error when distributing sort folder [" + sortingfolder
							+ "] on [" + STATIC.splitiport(ip, String.valueOf(port)) + "]");
					e.printStackTrace();
					Sortfactory.clear(sortingfolder, Sortstatus.TERMINATE);
				}
				return Objectutil.convert(Sortstatus.get(sortingfolder));
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
