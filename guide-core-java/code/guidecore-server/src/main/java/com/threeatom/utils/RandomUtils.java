package com.threeatom.utils;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomUtils {
	public static final String ALLCHAR    = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 返回一个定长的随机纯字母字符串(只包含大小写字母)
	 */
	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = null;
		try {
			random = SecureRandom.getInstanceStrong();
			for (int i = 0; i < length; i++) {
				sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
			}
		} catch (NoSuchAlgorithmException e) {

		}
		return sb.toString();
	}

	/**
	 *  返回一个定长的随机纯小写字母字符串
	 */
	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}

	/**
	 *  返回一个定长的随机纯大写字母字符串
	 */
	public static String generateUpperString(int length) {
		return generateMixString(length).toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println("返回一个定长的随机纯字母字符串(只包含大小写字母):" + generateMixString(16));
		System.out.println("返回一个定长的随机纯小写字母字符串:" + generateLowerString(16));

	}

	public static String get8UUID(){
		UUID id=UUID.randomUUID();
		String[] idd=id.toString().split("-");
		return idd[0];
	}

	public static String getUUID(int len) {
		if (0 >= len) {
			return null;
		}
		String uuid = UUID.randomUUID().toString().replace("-", "");
		StringBuffer str = new StringBuffer();
		long l = System.currentTimeMillis();
		String time = String.valueOf(l);
		for (int i = 0; i < len; i++) {
			str.append(uuid.charAt(i));
		}
		return str.toString();
	}
}
