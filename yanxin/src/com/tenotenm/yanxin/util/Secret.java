package com.tenotenm.yanxin.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Secret {

	private static final String key = "Pwm4$qrt*c68k#xa";

	public static String enc(String raw) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			return URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal(raw.getBytes("UTF-8"))),
					"UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	public static String dec(String enc) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(URLDecoder.decode(enc, "UTF-8"))), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
		
}
