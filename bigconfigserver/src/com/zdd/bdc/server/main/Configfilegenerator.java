package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filekvutil;

public class Configfilegenerator {
	private static void gencore() throws Exception {
		Path configfile = Filekvutil.configfile(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.configcreate("configserverip", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.configcreate("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.configcreate("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Filekvutil.configcreate("sessionexpireseconds", "3600", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.configcreate("itemsonepage", "100", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}

	private static void genunicorndig() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_DIG);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.configcreate("active", STATIC.splitenc("dig0","dig1","dig2"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		
		Filekvutil.configcreate("dig0.sort", STATIC.splitenc("testable", "sort"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig0.sequence", STATIC.SORT_SEQUENCE(true), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig0.interval", "W31644", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig0.period", STATIC.splitfromto("20180910","20181210"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig0.index", "testdigindex", "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig0.filter", STATIC.splitenc( "testable", "filter1"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		
		Filekvutil.configcreate("dig1.sort", STATIC.splitenc( "testable", "sort"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig1.sequence", STATIC.SORT_SEQUENCE(false), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig1.interval", "D1644", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig1.period", STATIC.splitfromto("20180910","20200920"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig1.index", STATIC.splitenc("testable", "index"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig1.filter", STATIC.splitenc("testable", "filter1", "testable", "filter2"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);

		Filekvutil.configcreate("dig2.sort", STATIC.splitenc("testable", "sort"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig2.sequence", STATIC.SORT_SEQUENCE(false), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig2.interval", "D1644", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig2.period", STATIC.splitfromto("20180910","20200920"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig2.index", STATIC.splitenc("testable", "index"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.configcreate("dig2.filter", "", "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
}

	private static void genpending() throws Exception {
		Path configfile = Filekvutil.configfile(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

//		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9991"), "pending", STATIC.NAMESPACE_CORE,
//				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9992"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
/*		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9993"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9994"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9995"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9996"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9997"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.configcreate(STATIC.splitiport("127.0.0.1","9998"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
*/		 
	}

	private static void genpngbigfrom() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigfrom", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.configcreate("active", STATIC.splitenc("20181023",""), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigpng2", "10", "127.0.0.1", "9997"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigpng1", "10", "127.0.0.1", "9998"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genpngbigto() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigto", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.configcreate("active", STATIC.splitenc("20181023",""), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigpng2", "10", "127.0.0.1", "9995"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigpng1", "10", "127.0.0.1", "9996"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigdata() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.configcreate("active", STATIC.splitenc("20181023",""), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigdata2", "100", "127.0.0.1", "9993"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.configcreate("20181023", STATIC.splitenc("bigdata1", "100", "127.0.0.1", "9994"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigindex() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.configcreate("active", STATIC.splitenc(STATIC.splitfromto("0","9"),STATIC.splitfromto("10","19")), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		
		//Filekvutil.configcreate("active", STATIC.splitenc(STATIC.splitfromto("0","19"),""), "unicorn",
		//				STATIC.REMOTE_CONFIG_BIGINDEX);
				
		//Filekvutil.configcreate(STATIC.splitfromto("0","19"), STATIC.splitenc("bigindex1", "100", "127.0.0.1", "9991"), "unicorn",
		//		STATIC.REMOTE_CONFIG_BIGINDEX);
		
		Filekvutil.configcreate(STATIC.splitfromto("0","9"), STATIC.splitenc("bigindex1", "100", "127.0.0.1", "9991"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		Filekvutil.configcreate(STATIC.splitfromto("10","19"), STATIC.splitenc("bigindex2", "100", "127.0.0.1", "9992"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		
		//add here
	}

	public static void main(String[] s) throws Exception {
		gencore();
		genunicorndig();
		genpending();
		genpngbigfrom();
		genpngbigto();
		genunicornbigdata();
		genunicornbigindex();
	}
}
