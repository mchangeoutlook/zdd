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

		Filekvutil.config("configserverip", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.config("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.config("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Filekvutil.config("sessionexpireseconds", "3600", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.config("itemsonepage", "100", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}

	private static void gendig() throws Exception {
		Path configfile = Filekvutil.configfile(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("active", STATIC.splitenc("dig0","dig1"), STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		
		Filekvutil.config("dig0.sort", "testindex", STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.sequence", STATIC.SORT_SEQUENCE(true), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.interval", "W01300", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.period", STATIC.splitfromto("20180910","20180910"), STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.index", STATIC.splitenc("namespace0", "table0", "col0", "table1", "col1"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.filter", STATIC.splitenc("namespace0", "table0", "col0", "table1", "col1"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		
		Filekvutil.config("dig1.sort", STATIC.splitenc("unicorn", "table", "col"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.sequence", STATIC.SORT_SEQUENCE(false), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.interval", "D1300", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.period", STATIC.splitfromto("20180910","20200920"), STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.index", STATIC.splitenc("namespace0", "table0", "col0"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.filter", STATIC.splitenc("namespace0", "table0", "col0"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
	}

	private static void genpending() throws Exception {
		Path configfile = Filekvutil.configfile(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9991"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9992"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9993"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9994"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9995"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9996"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9997"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		 Filekvutil.config(STATIC.splitiport("127.0.0.1","9998"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
	}

	private static void genpngbigfrom() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigfrom", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", STATIC.splitenc("20181023",""), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", STATIC.splitenc("bigpng2", "10", "127.0.0.1", "9997"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", STATIC.splitenc("bigpng1", "10", "127.0.0.1", "9998"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genpngbigto() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigto", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", STATIC.splitenc("20181023",""), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", STATIC.splitenc("bigpng2", "10", "127.0.0.1", "9995"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", STATIC.splitenc("bigpng1", "10", "127.0.0.1", "9996"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigdata() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", STATIC.splitenc("20181023",""), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", STATIC.splitenc("bigdata2", "100", "127.0.0.1", "9993"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", STATIC.splitenc("bigdata1", "100", "127.0.0.1", "9994"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigindex() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("active", STATIC.splitenc(STATIC.splitfromto("0","9"),STATIC.splitfromto("10","19")), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		
		Filekvutil.config(STATIC.splitfromto("0","9"), STATIC.splitenc("bigindex1", "100", "127.0.0.1", "9991"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		Filekvutil.config(STATIC.splitfromto("10","19"), STATIC.splitenc("bigindex2", "100", "127.0.0.1", "9992"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
		//add here
	}

	public static void main(String[] s) throws Exception {
		gencore();
		gendig();
		genpending();
		genpngbigfrom();
		genpngbigto();
		genunicornbigdata();
		genunicornbigindex();
	}
}
