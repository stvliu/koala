package org.openkoala.cas.casmanagement.infra.password;

import java.security.MessageDigest;
import java.util.logging.Logger;

/**
 * 盐值加密
 * @author zyb
 * @since 2013-7-16 下午7:02:35
 */
public class PasswordEncoder {

	private static final String ENCODING_UTF8 = "utf-8";

	private static final String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f" };

	private Object salt;
	private String algorithm;
	
	private static final Logger LOGGER = Logger.getLogger("PasswordEncoder");

	public PasswordEncoder(Object salt, String algorithm) {
		this.salt = salt;
		this.algorithm = algorithm;
	}

	/**
	 * 加密
	 * @param rawPass
	 * @return
	 */
	public String encode(String rawPass) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			// 加密后的字符串
			result = byteArrayToHexString(md.digest(mergePasswordAndSalt(rawPass).getBytes(ENCODING_UTF8)));
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}
		return result;
	}

	/**
	 * 密码是否正确
	 * @param encPass
	 * @param rawPass
	 * @return
	 */
	public boolean isPasswordValid(String encPass, String rawPass) {
		String pass1 = "" + encPass;
		String pass2 = encode(rawPass);
		return pass1.equals(pass2);
	}

	/**
	 * 合并密码和盐值
	 * @param password
	 * @return
	 */
	private String mergePasswordAndSalt(String password) {
		if ((salt == null) || "".equals(salt)) {
			return password;
		}
		return password + "{" + salt.toString() + "}";
	}

	/**
	 * 将字节数组转换成16进制字符串
	 * 
	 * @param b 字节数组
	 * @return 	16进制字串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 将字节转成16进制字符串
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return HEX_DIGITS[d1] + HEX_DIGITS[d2];
	}

}