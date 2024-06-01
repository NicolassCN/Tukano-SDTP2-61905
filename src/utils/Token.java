package utils;

import java.sql.Time;

import com.google.protobuf.Timestamp;

public class Token {

	private static String val;
	
	public static void set(String token) {
		val = token;
	}
	
	public static String get() {
		return val;
		
	}
	
	public static boolean matches(String token) {
		return (val != null) && (token != null) &&  val.equals( token );
	}
}
