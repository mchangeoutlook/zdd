package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configfilegeneratoryanxinlocal {
	private static void gencore() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("configserverip", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}
	
	private static void genbigfilefrom() throws Exception {
		Path configfile = Fileconfigutil.file("bigfilefrom", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "bigfilefrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile1", "0", "127.0.0.1", "9998"), "bigfilefrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile2", "0", "127.0.0.1", "9997"), "bigfilefrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genbigfileto() throws Exception {
		Path configfile = Fileconfigutil.file("bigfileto", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "bigfileto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile1", "0", "127.0.0.1", "9996"), "bigfileto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile2", "0", "127.0.0.1", "9995"), "bigfileto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genyanxincore() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
/*
		Fileconfigutil.create("session.expire.seconds", "3600", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("wrongpass.wait.seconds", "3600", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("ipdeny.wait.seconds", "3600", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("freeuse.days", "30", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("account.reuse.in.days", "60", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("first.login.in.seconds", "600", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "200", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.in.days", "30", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.days.max", "180", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("days.togive.max", "10000", namespace, STATIC.REMOTE_CONFIG_CORE);
*/
		Fileconfigutil.create("session.expire.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("wrongpass.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("ipdeny.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("freeuse.days", "1", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("account.reuse.in.days", "1", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("first.login.in.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "2", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.in.days", "2", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.days.max", "2", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("days.togive.max", "100", namespace, STATIC.REMOTE_CONFIG_CORE);

		Fileconfigutil.create("admins", STATIC.splitenc("afs",""), namespace, STATIC.REMOTE_CONFIG_CORE);

	}
	
	private static void genbiguniqueindex() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("servergroups0", "2", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create("servergroups00", STATIC.splitenc("biguniqueindex1","127.0.0.1", "9994"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups01", STATIC.splitenc("biguniqueindex2","127.0.0.1", "9993"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Yxaccount-ukey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Yxaccountkey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

	}
		
	private static void genbigdata() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata1", "5000", "127.0.0.1", "9992"), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata2", "5000", "127.0.0.1", "9991"), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}
	
	private static void genbigpagedindex() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc(STATIC.splitfromto("0","2499"),STATIC.splitfromto("2500","4999")), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("0","2499"), STATIC.splitenc("bigpagedindex1", "100", "127.0.0.1", "9990"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("2500","4999"), STATIC.splitenc("bigpagedindex2", "100", "127.0.0.1", "9989"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		
	}

	private static void genpending() throws Exception {
	Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9992"), "pending", STATIC.NAMESPACE_CORE,
			 STATIC.REMOTE_CONFIG_PENDING);
	}

	public static void main(String[] s) throws Exception {
		gencore();
		genbigfilefrom();
		genbigfileto();
		genbiguniqueindex();
		genbigdata();
		genyanxincore();
		genpending();
		genbigpagedindex();
	}
}
