package com.zdd.bdc.server.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Fileconfigutil;

public class Configfilegeneratoryanxinlocal {
	private static void gencore() throws Exception {
		Path configfile = Fileconfigutil.file(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		Fileconfigutil.create("CONFIGSERVER_IP", "127.0.0.1", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("updateconfigcache.intervalseconds", "30", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("CONFIGSERVER_PORT", "9999", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);

		Fileconfigutil.create("command.inode", "df -i", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("command.space", "df", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("command.mem", "free", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		
		Fileconfigutil.create("notify.receiver", "443370165@qq.com", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("notify.sender", "monitor@xinzyan.com", STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE);
	}
	
	private static void genyanxinbigdata() throws Exception {
		String namespace =  "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIGFILE_BIGDATA, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		String appdata = "data";
		String appfile = "file";
		
		Fileconfigutil.create(appdata+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX, STATIC.splitenc("20190118","20190701"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		
		Fileconfigutil.create(STATIC.splitenc(appdata, "20190118"), STATIC.splitenc("bigdata0", "5000", "127.0.0.1", "9998"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		Fileconfigutil.create(STATIC.splitenc(appdata, "20190118"), STATIC.splitenc("bigdata1", "5000", "127.0.0.1", "9997"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		
		Fileconfigutil.create(STATIC.splitenc(appdata, "20190701"), STATIC.splitenc("bigdata2", "5000", "127.0.0.1", "9988"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		Fileconfigutil.create(STATIC.splitenc(appdata, "20190701"), STATIC.splitenc("bigdata3", "5000", "127.0.0.1", "9987"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		
		Fileconfigutil.create(appfile+STATIC.REMOTE_CONFIGKEY_SERVERGROUPSSUFFIX, STATIC.splitenc("20190118",""), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		
		Fileconfigutil.create(STATIC.splitenc(appfile, "20190118"), STATIC.splitenc("bigfiles0", "0", "127.0.0.1", "9996"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		Fileconfigutil.create(STATIC.splitenc(appfile, "20190118"), STATIC.splitenc("bigfiles1", "0", "127.0.0.1", "9995"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGDATA);
		
	}

	private static void genyanxincore() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIGFILE_CORE, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
/*
		Fileconfigutil.create("session.expire.seconds", "3600", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("wrongpass.wait.seconds", "3600", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("ipdeny.wait.seconds", "3600", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("freeuse.days", "30", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("account.reuse.in.days", "60", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("onepage.items", "20", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "200", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.expire.in.days", "30", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.expire.days.max", "180", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.all.expire.times.days", "0.0", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("days.togive.max", "10000", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("wrongpass.times.max", "5", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("photo.datavanvasurl.max", "150000", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("photosmall.datavanvasurl.max", "50000", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("read.stat.days", "7", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
*/
		Fileconfigutil.create("session.expire.seconds", "120", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("wrongpass.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("ipdeny.wait.seconds", "120", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("freeuse.days", "2", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("account.reuse.in.days", "1", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("onepage.items", "20", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("everyday.everyip.newaccounts.max", "200", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.expire.in.days", "1", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.expire.days.max", "2", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("extend.all.expire.times.days", "0.0", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		
		Fileconfigutil.create("days.togive.max", "100", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("wrongpass.times.max", "3", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("photo.datavanvasurl.max", "150000", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("photosmall.datavanvasurl.max", "50000", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("read.stat.days", "7", namespace, STATIC.REMOTE_CONFIGFILE_CORE);

		Fileconfigutil.create("forgetpass", "我们不提供任何方式帮你找回密码，因为任何找回密码的方式都有可能泄露你的隐私。由于人脑对图形的记忆比文字更深刻，所以我们使用点击格子的次数和顺序来代替传统文字密码，每个格子都可以重复点击，重复点击的次数和顺序也将计入你的密码", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.items", "6", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.title", "你的言心协议", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.0", "1. 逾回收时间，你的账号将不能登录，该账号相关日记也无法找回，延长过期时间将自动延长回收时间", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.1", "2. 逾过期时间，你可以登录账号，并延长过期时间，但无法查看该账号任何日记，也无法编辑当天日记", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.2", "3. 我们承诺永不主动泄露你的隐私，并且你的密码被MD5加密，你的所有日记内容被AES加密", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.3", "4. 我们使用腾讯云存储为你的数据保驾护航，但如果由于外界不可抗力因素导致对你的账号相关数据造成无法挽回的损坏或丢失，我们不承担任何赔偿责任，感谢你的理解和支持", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.4", "5. {0}"
				, namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("agreement.item.5", "6. 账号一旦注册成功，视为你已经与 心之言（上海）科技有限公司 达成以上所有条款", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		Fileconfigutil.create("buyhint", "购买过期时间延长券，请前往：淘宝，搜索店铺：言心", namespace, STATIC.REMOTE_CONFIGFILE_CORE);
		
		Fileconfigutil.create("admins", STATIC.splitenc("4312f3c064d02201946f4de99406716e535b0bca","f90239205cf9820193a64524bc0d69e67a5a8b34"), namespace, STATIC.REMOTE_CONFIGFILE_CORE);

	}
	
	private static void genyanxinbiguniqueindex() throws Exception {
		String namespace = "yanxin";
		Path configfile = Fileconfigutil.file(namespace, STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX, true);
		if (Files.exists(configfile)) {
			Files.write(configfile, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.SYNC,
					StandardOpenOption.TRUNCATE_EXISTING);
		}

		String servergroupsaccount = "sgaccount0";
		Fileconfigutil.create(servergroupsaccount+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsaccount+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsaccount+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(servergroupsaccount, STATIC.splitenc("accounts0","127.0.0.1", "9994"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsaccount, STATIC.splitenc("accounts1","127.0.0.1", "9993"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

		String servergroupsyanxin = "sgyanxin0";
		Fileconfigutil.create(servergroupsyanxin+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsyanxin+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsyanxin+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(servergroupsyanxin, STATIC.splitenc("yanxins0","127.0.0.1", "9992"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupsyanxin, STATIC.splitenc("yanxins1","127.0.0.1", "9991"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

		String servergroupspaged = "sgpaged0";
		Fileconfigutil.create(servergroupspaged+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_ROOTRANGESUFFIX, STATIC.splitfromto("8","16"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupspaged+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYKEYMAXSUFFIX, "60", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupspaged+STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_CAPACITYVALUESUFFIX, "40", namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(servergroupspaged, STATIC.splitenc("paged0","127.0.0.1", "9990"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		Fileconfigutil.create(servergroupspaged, STATIC.splitenc("paged1","127.0.0.1", "9989"), namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

		Fileconfigutil.create("Yxaccount-ukey", servergroupsaccount, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Yxaccount-ukey", servergroupsaccount, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

		Fileconfigutil.create("Yanxin-ukey", servergroupsyanxin, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"Yanxin-ukey", servergroupsyanxin, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

		Fileconfigutil.create("pagedkey", servergroupspaged, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);
		
		Fileconfigutil.create(STATIC.REMOTE_CONFIGKEY_BIGUNIQUEINDEX_SCALEPREFIX+"pagedkey", servergroupspaged, namespace,
				STATIC.REMOTE_CONFIGFILE_BIGUNIQUEINDEX);

	}
		
	private static void genpending() throws Exception {
	Fileconfigutil.create(STATIC.splitiport("127.0.0.1","9998"), "", STATIC.NAMESPACE_CORE,
			 STATIC.REMOTE_CONFIGFILE_PENDING);
	}

	public static void main(String[] s) throws Exception {
		gencore();
		genyanxinbiguniqueindex();
		genyanxinbigdata();
		genyanxincore();
		genpending();
	}
}
