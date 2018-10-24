package com.zdd.bdc.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.util.Filekvutil;
import com.zdd.bdc.util.STATIC;

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
	}

	private static void gendig() throws Exception {
		Path configfile = Filekvutil.configfile(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("active", "dig0", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("active", "dig1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);

		Filekvutil.config("dig0.sort", STATIC.splitenc("namespace", "table", "col"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.interval", "W01300", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.period", "20180910-20180910", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.index", STATIC.splitenc("namespace0", "table0", "col0"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.index", STATIC.splitenc("namespace1", "table1", "col1"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.filter", STATIC.splitenc("namespace0", "table0", "col0"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig0.filter", STATIC.splitenc("namespace1", "table1", "col1"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);

		Filekvutil.config("dig1.sort", STATIC.splitenc("namespace", "table", "col"), STATIC.NAMESPACE_CORE,
				STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.interval", "D1300", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
		Filekvutil.config("dig1.period", "20180910-20200920", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_DIG);
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

		// Filekvutil.config("127.0.0.1#9999", "pending", STATIC.NAMESPACE_CORE,
		// STATIC.REMOTE_CONFIG_PENDING);
	}

	private static void genpngbigfrom() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigfrom", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("20180802", STATIC.splitenc("bigpng1", "10000", "127.0.0.1", "9998"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20180802", STATIC.splitenc("bigpng2", "10000", "127.0.0.1", "9997"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genpngbigto() throws Exception {
		Path configfile = Filekvutil.configfile("pngbigto", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("20180802", STATIC.splitenc("bigpng1", "10000", "127.0.0.1", "9996"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20180802", STATIC.splitenc("bigpng2", "10000", "127.0.0.1", "9995"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigdata() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("20180802", STATIC.splitenc("bigdata1", "10000", "127.0.0.1", "9994"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Filekvutil.config("20180802", STATIC.splitenc("bigdata2", "10000", "127.0.0.1", "9993"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
	}

	private static void genunicornbigindex() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_BIGINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Filekvutil.config("0-9999", STATIC.splitenc("bigindex1", "10000", "127.0.0.1", "9991"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGINDEX);
	}

	private static void genunicorncore() throws Exception {
		Path configfile = Filekvutil.configfile("unicorn", STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		Filekvutil.config("sessionexpireseconds", "3600", "unicorn", STATIC.REMOTE_CONFIG_CORE);
		Filekvutil.config("itemsonepage", "100", "unicorn", STATIC.REMOTE_CONFIG_CORE);
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
