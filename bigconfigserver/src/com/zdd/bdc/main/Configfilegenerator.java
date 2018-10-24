package com.zdd.bdc.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.util.CS;
import com.zdd.bdc.util.Filekvutil;
import com.zdd.bdc.util.SS;

public class Configfilegenerator {
	private static void gencore() throws Exception {
		Path configfile = Filekvutil.configfile(CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("configserverip", "127.0.0.1", CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE);
		Filekvutil.config("updateconfigcache.intervalseconds", "30", CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE);
		Filekvutil.config("configserverport", "9999", CS.NAMESPACE_CORE, CS.REMOTE_CONFIG_CORE);
	}

	private static void gendig() throws Exception {
		Path configfile = Filekvutil.configfile(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("active", "dig0", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("active", "dig1", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);

		Filekvutil.config("dig0.sort", CS.splitenc("namespace", "table", "col"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.interval", "W01300", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.period", "20180910-20180910", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.index", CS.splitenc("namespace0", "table0", "col0"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.index", CS.splitenc("namespace1", "table1", "col1"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.filter", CS.splitenc("namespace0", "table0", "col0"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.filter", CS.splitenc("namespace1", "table1", "col1"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);

		Filekvutil.config("dig1.sort", CS.splitenc("namespace", "table", "col"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.interval", "D1300", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.period", "20180910-20200920", CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.index", CS.splitenc("namespace0", "table0", "col0"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.filter", CS.splitenc("namespace0", "table0", "col0"), CS.NAMESPACE_CORE,
				SS.REMOTE_CONFIG_DIG);
	}

	private static void genpending() throws Exception {
		Path configfile = Filekvutil.configfile(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		// Filekvutil.config("127.0.0.1#9999", "pending", STATIC.NAMESPACE_CORE,
		// STATIC.REMOTE_CONFIG_PENDING);
	}

	private static void genpngbigfrom() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigfrom", CS.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", CS.splitenc("20181023",""), "pngbigfrom",
				CS.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", CS.splitenc("bigpng2", "10000", "127.0.0.1", "9997"), "pngbigfrom",
				CS.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", CS.splitenc("bigpng1", "10000", "127.0.0.1", "9998"), "pngbigfrom",
				CS.REMOTE_CONFIG_BIGDATA);
	}

	private static void genpngbigto() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigto", CS.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", CS.splitenc("20181023",""), "pngbigto",
				CS.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", CS.splitenc("bigpng2", "10000", "127.0.0.1", "9995"), "pngbigto",
				CS.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", CS.splitenc("bigpng1", "10000", "127.0.0.1", "9996"), "pngbigto",
				CS.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigdata() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", CS.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Filekvutil.config("active", CS.splitenc("20181023",""), "unicorn",
				CS.REMOTE_CONFIG_BIGDATA);
		
		//add here
		Filekvutil.config("20181023", CS.splitenc("bigdata2", "10000", "127.0.0.1", "9993"), "unicorn",
				CS.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20181023", CS.splitenc("bigdata1", "10000", "127.0.0.1", "9994"), "unicorn",
				CS.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigindex() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", CS.REMOTE_CONFIG_BIGINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("active", CS.splitenc("0-9999",""), "unicorn",
				CS.REMOTE_CONFIG_BIGINDEX);
		
		Filekvutil.config("0-9999", CS.splitenc("bigindex1", "10000", "127.0.0.1", "9991"), "unicorn",
				CS.REMOTE_CONFIG_BIGINDEX);
		//add here
	}

	private static void genunicorncore() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", CS.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		Filekvutil.config("sessionexpireseconds", "3600", "unicorn", CS.REMOTE_CONFIG_CORE);
		Filekvutil.config("itemsonepage", "100", "unicorn", CS.REMOTE_CONFIG_CORE);
	}

	public static void main(String[] s) throws Exception {
		gencore();
		gendig();
		genpending();
		genpngbigfrom();
		genpngbigto();
		genunicornbigdata();
		genunicornbigindex();
		genunicorncore();
	}
}
