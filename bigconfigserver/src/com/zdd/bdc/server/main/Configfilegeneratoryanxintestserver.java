package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configfilegeneratoryanxintestserver {
	private static void gencore() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		Fileconfigutil.create("configserverip", "172.17.0.2", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("configserverport", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIG_CORE);
	}
	
	private static void genbigfilefrom() throws Exception {
		Path configfile = Fileconfigutil.file("bigfilefrom", STATIC.REMOTE_CONFIG_BIGDATA, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		
		Fileconfigutil.create("active", STATIC.splitenc("20190118",""), "bigfilefrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile1", "0", "172.17.0.2", "9998"), "bigfilefrom",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile2", "0", "172.17.0.2", "9997"), "bigfilefrom",
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
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile1", "0", "172.17.0.2", "9996"), "bigfileto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigfile2", "0", "172.17.0.2", "9995"), "bigfileto",
				STATIC.REMOTE_CONFIG_BIGDATA);
		//add more
	}

	private static void genyanxincore() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIG_CORE, true);
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
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "200", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.in.days", "30", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.days.max", "180", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("days.togive.expire.in.days", "180", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("days.togive.max", "10000", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("wrongpass.times.max", "5", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("photo.datavanvasurl.max", "150000", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("photosmall.datavanvasurl.max", "50000", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("read.stat.days", "7", namespace, STATIC.REMOTE_CONFIG_CORE);
*/
		Fileconfigutil.create("session.expire.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("wrongpass.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("ipdeny.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("freeuse.days", "2", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("account.reuse.in.days", "1", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "200", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.in.days", "1", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("extend.expire.days.max", "2", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("days.togive.expire.in.days", "1", namespace, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("days.togive.max", "100", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("wrongpass.times.max", "3", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("photo.datavanvasurl.max", "150000", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("photosmall.datavanvasurl.max", "50000", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("read.stat.days", "7", namespace, STATIC.REMOTE_CONFIG_CORE);

		Fileconfigutil.create("forgetpass", "我们不提供任何方式帮你找回密码，因为任何找回密码的方式都有可能泄露你的隐私。由于人脑对图形的记忆比文字更深刻，所以我们使用点击格子的次数和顺序来代替传统文字密码，每个格子都可以重复点击，重复点击的次数和顺序也将计入你的密码", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.items", "6", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.title", "你的言心协议", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.0", "1. 为增强你的隐私安全，我们使用AES加密算法对你的日记内容和图片进行加密存储", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.1", "2. 在你的账号过期时间之前，你可登录该账号并查看该账号的所有日记和编辑当天的日记，也可根据系统提示延长过期时间，过期后并且在该账号被回收之前，你可登录该账号并根据系统提示延长过期时间，但不可查看和编辑该账号的任何日记", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.2", "3. 在你的账号回收时间之前，你可登录该账号并延长过期时间，如果因为没有及时延长该账号的过期时间导致该账号被回收，回收后视为你主动放弃该账号，你将无法登录该账号，也无法再延长过期时间，该账号的所有日记和相关数据也将无法找回", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.3", "4. 在你的账号回收时间之前，我们时刻努力确保该账号的日记和相关数据安全，但如果由于外界不可抗力因素导致该账号的日记和相关数据损失，我们深感抱歉，并且我们会根据具体原因和损失情况竭尽全力进行挽救，在无法完成100%挽救的情况下，我们会坦诚的把原因和结果告知你，但我们不承担任何赔偿责任，感谢你的理解和支持", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.4", "5. {0}"
				, namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("agreement.item.5", "6. 账号一旦注册成功，视为你已经与 心之言（上海）科技有限公司 达成以上所有条款", namespace, STATIC.REMOTE_CONFIG_CORE);
		Fileconfigutil.create("buyhint", "购买过期时间延长券，请前往：淘宝，搜索店铺：言心", namespace, STATIC.REMOTE_CONFIG_CORE);
		
		Fileconfigutil.create("admins", STATIC.splitenc("f90239205cf9820193a64524bc0d69e67a5a8b34","f90239205cf9820193a64524bc0d69e67a5a8b34"), namespace, STATIC.REMOTE_CONFIG_CORE);

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
		
		Fileconfigutil.create("servergroups00", STATIC.splitenc("biguniqueindex1","172.17.0.2", "9994"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		Fileconfigutil.create("servergroups01", STATIC.splitenc("biguniqueindex2","172.17.0.2", "9993"), namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Yxaccount-ukey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Yxaccount-ukey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);

		Fileconfigutil.create("Yanxin-ukey", "servergroups0", namespace,
				STATIC.REMOTE_CONFIG_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Yanxin-ukey", "servergroups0", namespace,
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
		
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata1", "5000", "172.17.0.2", "9992"), namespace,
				STATIC.REMOTE_CONFIG_BIGDATA);
		Fileconfigutil.create("20190118", STATIC.splitenc("bigdata2", "5000", "172.17.0.2", "9991"), namespace,
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
		Fileconfigutil.create(STATIC.splitfromto("0","2499"), STATIC.splitenc("bigpagedindex1", "100", "172.17.0.2", "9990"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		Fileconfigutil.create(STATIC.splitfromto("2500","4999"), STATIC.splitenc("bigpagedindex2", "100", "172.17.0.2", "9989"), namespace,
				STATIC.REMOTE_CONFIG_BIGPAGEDINDEX);
		
	}

	private static void genpending() throws Exception {
	Fileconfigutil.create(STATIC.splitiport("172.17.0.2","9992"), "pending", STATIC.NAMESPACE_CORE,
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
