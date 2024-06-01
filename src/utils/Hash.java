package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
		static MessageDigest md5;
		static MessageDigest sha256;
				
		public static synchronized byte[] md5( byte[] data ) {
			if (md5 == null) {
				try {
					md5 = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			md5.reset();
			md5.update( data == null ? new byte[0] : data );
			return md5.digest();
		}
		
		public static synchronized byte[] sha256( byte[] data ) {
			if (sha256 == null) {
				try {
					sha256 = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			sha256.reset();
			sha256.update( data == null ? new byte[0] : data );
			return sha256.digest();
		}

		public static String sha256(String data) {
			return of(sha256(data.getBytes()));
		}

		public static String of(byte[] hash) {
			StringBuilder sb = new StringBuilder();
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}
}
