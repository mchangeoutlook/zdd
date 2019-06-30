package com.zdd.bdc.server.util;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;
import com.zdd.bdc.client.util.Objectutil;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.biz.Configserver;

public class Monitor {

	private static final Map<String, Long> folderiport_time = new Hashtable<String, Long>();

	public static void updateclientime(String folderiport) {
		folderiport_time.put(folderiport, System.currentTimeMillis());
	}

	public static void start(StringBuffer pending) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println(new Date() + " ==== Monitor starts.");
				while (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(pending.toString())) {
					try {
						Thread.sleep(2 * Integer.parseInt(Configserver.readconfig(STATIC.NAMESPACE_CORE,
								STATIC.REMOTE_CONFIGFILE_CORE, STATIC.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS)));
					} catch (Exception e) {
						// do nothing
					}
					try {
						@SuppressWarnings("unchecked")
						Map<String, Long> temp = (Map<String, Long>) Objectutil
								.convert(Objectutil.convert(folderiport_time));
						for (String key : temp.keySet()) {
							if (System.currentTimeMillis() - temp.get(key) > 2
									* 1000 * Integer.parseInt(Configserver.readconfig(STATIC.NAMESPACE_CORE,
											STATIC.REMOTE_CONFIGFILE_CORE, STATIC.REMOTE_CONFIGKEY_UPDATECONFIGCACHEINTERVALS))) {
								folderiport_time.remove(key);
								qqemail(Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE,
										"notify.sender"),
										Configserver.readconfig(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_CORE,
												"notify.receiver"),
										"down suspect", key);
							}
						}
					} catch (Exception e) {
						// do nothing
					}
				}
				System.out.println(new Date() + " ==== Monitor exits.");
			}

		}).start();

		
	}

	public static void qqemail(String sender, String receiver, String title, String content) {
		try {
			System.out.println(new Date() + " ==== sending [" + title + "] [" + content + "] to [" + receiver + "]");
			STATIC.ES.execute(new Runnable() {

				@Override
				public void run() {
					try {
						Properties prop = new Properties();
						prop.setProperty("mail.transport.protocol", "smtp");
						prop.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
						prop.setProperty("mail.smtp.port", "465");
						prop.setProperty("mail.smtp.auth", "true");
						MailSSLSocketFactory sf = new MailSSLSocketFactory();
						sf.setTrustAllHosts(true);
						prop.put("mail.smtp.ssl.enable", "true");
						prop.put("mail.smtp.ssl.socketFactory", sf);

						Session s = Session.getDefaultInstance(prop, new Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(sender, "mCHrmo@1376$");
							}
						});
						MimeMessage msg = new MimeMessage(s);
						msg.setFrom(new InternetAddress(sender, sender));
						msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver, false));
						msg.setSubject(title);
						Date now = new Date();
						StringBuilder sb = new StringBuilder(
								"<html><body>" + "<div style='width:100%'>" + content + "</div>" + "</body></html>");
						msg.setContent(sb.toString(), "text/html;charset=UTF-8");
						msg.setSentDate(now);
						Transport.send(msg);
					} catch (Exception e) {
						// do nothing
					}
				}

			});
		} catch (Exception e) {
			// do nothing
		}
	}

}
