package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configfilegenerator {
	private static void gencore() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("configserverip", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("sessionexpireseconds", "14400", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("itemsonepage", "100", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("namespace.jedge", "jedge", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("namespace.hm", "jedge", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}
	

	private static void genpngbigfrom() throws Exception {
		Path configfile = Fileconfigutil.file("pngbigfrom", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigpng1", "0", "127.0.0.1", "9998"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigpng2", "0", "127.0.0.1", "9997"), "pngbigfrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genpngbigto() throws Exception {
		Path configfile = Fileconfigutil.file("pngbigto", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigpng1", "0", "127.0.0.1", "9996"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigpng2", "0", "127.0.0.1", "9995"), "pngbigto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	

	private static void genjedgebiguniqueindex() throws Exception {
		Path configfile = Fileconfigutil.file("jedge", STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("servergroups0", "2", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create("servergroups00", STATIC.splitenc("biguniqueindex1","127.0.0.1", "9989"), "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups01", STATIC.splitenc("biguniqueindex2","127.0.0.1", "9990"), "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Accountkey", "servergroups0", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Codekey-hm", "servergroups0", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Companykey", "servergroups0", "jedge",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

	}
		
	private static void genjedgebigdata() throws Exception {
		Path configfile = Fileconfigutil.file("jedge", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "jedge",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata1", "5000", "127.0.0.1", "9994"), "jedge",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata2", "5000", "127.0.0.1", "9993"), "jedge",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genjedgebigpagedindex() throws Exception {
		Path configfile = Fileconfigutil.file("jedge", STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		
		Fileconfigutil.create("active", STATIC.splitenc(STATIC.splitfromto("0","2499"),STATIC.splitfromto("2500","4999")), "jedge",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("0","2499"), STATIC.splitenc("bigpagedindex1", "100", "127.0.0.1", "9991"), "jedge",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("2500","4999"), STATIC.splitenc("bigpagedindex2", "100", "127.0.0.1", "9992"), "jedge",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		
	}
	
	private static void genjedgecore() throws Exception {
		
		Path configfile = Fileconfigutil.file("jedge", STATIC.REMOTE_CONFIG_CORE);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("activecoupon.eids", STATIC.splitenc("hm",""), "jedge",
				STATIC.REMOTE_CONFIG_CORE);
	}
	
	private static void genjedgehm() throws Exception {
		String eid = "hm";
		Path configfile = Fileconfigutil.file("jedge", eid);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		
		Fileconfigutil.create("code.apikey", "D5gZT3Q4TE%2Fhe8e8KKUFSUi19Y3xrV%2F11YB6J%2F3uCIlTPMwG83lfZbbc03CkPyG8", "jedge",
				eid);
		Fileconfigutil.create("code.signkey", "wGf2%2FkPLTNdL0yIbc2sxm2%2FbiBd3VPBAA17INviUky9TPMwG83lfZbbc03CkPyG8", "jedge",
				eid);
		
		Fileconfigutil.create("coupon.codeprotocolanddomain", "http://c.jedge.cn/", "jedge",
				eid);
		Fileconfigutil.create("coupon.uploadurl", "http://localhost:8080/jedgecode/code/upload", "jedge",
				eid);
		Fileconfigutil.create("coupon.activeurl", "http://localhost:8080/jedgecode/code/active", "jedge",
				eid);

		Fileconfigutil.create("pool2.incrementkey", "ec19eda01db9620194dd4ac295540e0857324398", "jedge",
				eid);
		Fileconfigutil.create("pool2.ratio", STATIC.splitenc("2","0.0212","1","0.0001034","3","0.9523","0","0.036"), "jedge",
				eid);
		
		Fileconfigutil.create("pool3.incrementkey", "ac19eda01db9620194dd4ac295540e0857324399", "jedge",
				eid);
		Fileconfigutil.create("pool3.ratio", STATIC.splitenc("2","0.312","1","0.0001034","3","0.1523","0","0.26"), "jedge",
				eid);
		
	}

	private static void genunicorndig() throws Exception {
		Path configfile = Fileconfigutil.file("unicorn", STATIC.REMOTE_CONFIG_DIG);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("active", STATIC.splitenc("dig1",""), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		
		Fileconfigutil.create("dig0.sort", STATIC.splitenc("testable1", "sort1"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.sequence", STATIC.SORT_SEQUENCE(true), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.interval", "W31220", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.period", STATIC.splitfromto("20180910","20181210"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.index", STATIC.splitenc("testable1", "index1"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig0.filter", STATIC.splitenc( "testable1", "filter11"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		
		Fileconfigutil.create("dig1.sort", STATIC.splitenc( "testable", "sort"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.sequence", STATIC.SORT_SEQUENCE(false), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.interval", "D1946", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.period", STATIC.splitfromto("20180910","20200920"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.index", STATIC.splitenc("testable", "index"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig1.filter", STATIC.splitenc("testable", "filter1", "testable", "filter2"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);

		Fileconfigutil.create("dig2.sort", STATIC.splitenc("testable3", "sort3"), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.sequence", STATIC.SORT_SEQUENCE(false), "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.interval", "D1220", "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.period", STATIC.splitfromto("20180910","20200920"), "unicorn", STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.index", "testfixedindex", "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
		Fileconfigutil.create("dig2.filter", "", "unicorn",
				STATIC.REMOTE_CONFIG_DIG);
}

	private static void genpending() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_PENDING);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

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
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9989"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9990"), "pending", STATIC.NAMESPACE_CORE,
				 STATIC.REMOTE_CONFIG_PENDING);
		
	}

	private static void genunicornbigdata() throws Exception {
		Path configfile = Fileconfigutil.file("unicorn", STATIC.REMOTE_CONFIG_BIGDATA);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20181023",""), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20181023", STATIC.splitenc("bigdata1", "5000", "127.0.0.1", "9994"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20181023", STATIC.splitenc("bigdata2", "5000", "127.0.0.1", "9993"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genunicornbigpagedindex() throws Exception {
		Path configfile = Fileconfigutil.file("unicorn", STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		/*
		Fileconfigutil.create("active", STATIC.splitenc(STATIC.splitfromto("0","9"),STATIC.splitfromto("10","19")), "unicorn",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("0","9"), STATIC.splitenc("bigpagedindex1", "100", "127.0.0.1", "9991"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("10","19"), STATIC.splitenc("bigpagedindex2", "100", "127.0.0.1", "9992"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
				*/
		
		
		Fileconfigutil.create("active", STATIC.splitenc(STATIC.splitfromto("0","19"),""), "unicorn",
						STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
				
		Fileconfigutil.create(STATIC.splitfromto("0","19"), STATIC.splitenc("bigpagedindex1", "100", "127.0.0.1", "9991"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		
		
	}

	private static void genunicornbiguniqueindex() throws Exception {
		Path configfile = Fileconfigutil.file("unicorn", STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("servergroups0", "2", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups0"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create("servergroups00", STATIC.splitenc("biguniqueindex1","127.0.0.1", "9989"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups01", STATIC.splitenc("biguniqueindex2","127.0.0.1", "9990"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("servergroups1", "3", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups1"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups1"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups1"+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create("servergroups10", STATIC.splitenc("biguniqueindexscale1","127.0.0.1", "9986"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups11", STATIC.splitenc("biguniqueindexscale2","127.0.0.1", "9987"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);		
		Fileconfigutil.create("servergroups12", STATIC.splitenc("biguniqueindexscale3","127.0.0.1", "9988"), "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Accountkey", "servergroups0", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Codekey-eid", "servergroups0", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Companykey-eid", "servergroups0", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"filter1", "servergroups0", "unicorn",
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
	}
	


	public static void main(String[] s) throws Exception {
		gencore();
		//genunicorndig();
		//genpending();
		genpngbigfrom();
		genpngbigto();
		//genunicornbigdata();
		//genunicornbigpagedindex();
		//genunicornbiguniqueindex();
		genjedgebiguniqueindex();
		genjedgebigdata();
		genjedgebigpagedindex();
		genjedgecore();
		genjedgehm();
	}
}
