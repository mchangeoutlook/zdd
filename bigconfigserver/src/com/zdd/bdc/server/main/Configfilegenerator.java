package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configfilegenerator {
	private static void gencore() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("configserverip", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("sessionexpireseconds", "14400", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("itemsonepage", "100", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}
	
	private static void genbigfilefrom() throws Exception {
		Path configfile = Fileconfigutil.file("bigfilefrom", STATIC.REMOTE_CONFIG_BIGDATA, true);
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
		Path configfile = Fileconfigutil.file("bigfileto", STATIC.REMOTE_CONFIG_BIGDATA, true);
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

	private static void genbiguniqueindex() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX, true);
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
		
		Fileconfigutil.create("servergroups00", STATIC.splitenc("biguniqueindex1","127.0.0.1", "9989"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups01", STATIC.splitenc("biguniqueindex2","127.0.0.1", "9990"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Accountkey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Accountkey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

	}
		
	private static void genbigdata() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGDATA, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata1", "5000", "127.0.0.1", "9994"), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata2", "5000", "127.0.0.1", "9993"), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genbigpagedindex() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_BIGPAGEDINDEX, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc(STATIC.splitfromto("0","2499"),STATIC.splitfromto("2500","4999")), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("0","2499"), STATIC.splitenc("bigpagedindex1", "100", "127.0.0.1", "9991"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("2500","4999"), STATIC.splitenc("bigpagedindex2", "100", "127.0.0.1", "9992"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		
	}

	private static void gendig() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_DIG, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("active", STATIC.splitenc("dig1",""), namespace, STATIC.REMOTE_CONFIG_DIG);
		
		Fileconfigutil.create("dig0.sort", STATIC.splitenc("testable1", "sort1"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.sequence", STATIC.SORT_SEQUENCE(true), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.interval", "W31220", namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.period", STATIC.splitfromto("20180910","20181210"), namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.index", STATIC.splitenc("testable1", "index1"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.filter", STATIC.splitenc( "testable1", "filter11"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		
		Fileconfigutil.create("dig1.sort", STATIC.splitenc( "testable", "sort"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.sequence", STATIC.SORT_SEQUENCE(false), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.interval", "D1946", namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.period", STATIC.splitfromto("20180910","20200920"), namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.index", STATIC.splitenc("testable", "index"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.filter", STATIC.splitenc("testable", "filter1", "testable", "filter2"), namespace,
				STATIC.REMOTE_CONFIG_DIG);

		Fileconfigutil.create("dig2.sort", STATIC.splitenc("testable3", "sort3"), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.sequence", STATIC.SORT_SEQUENCE(false), namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.interval", "D1220", namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.period", STATIC.splitfromto("20180910","20200920"), namespace, STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.index", "testfixedindex", namespace,
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.filter", "", namespace,
				STATIC.REMOTE_CONFIG_DIG);
}

	private static void genpending() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING, true);
		
		/*
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9991"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9992"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9993"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9994"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
				 
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","19993"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","19994"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9995"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9996"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9997"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9998"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9999"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		*/
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9991"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9992"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);

		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","19994"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","19993"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		
		
	}

	public static void main(String[] s) throws Exception {
		gencore();
		gendig();
		genpending();
		genbigfilefrom();
		genbigfileto();
		genbiguniqueindex();
		genbigdata();
		genbigpagedindex();
	}
}
