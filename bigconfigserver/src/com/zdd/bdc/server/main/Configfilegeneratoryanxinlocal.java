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

		Fileconfigutil.create("sessionexpireseconds", "3600", namespace, STATIC.REMOTE_CONFIG_CORE);

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

		Fileconfigutil.create("Yxaccountkey", "servergroups0", namespace,
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
	}
}
