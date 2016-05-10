package com.sap.als.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashCode {
	private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	 public static String getHashPassword(String password) {  		
		 String encodePassword = passwordEncoder.encode(password);  		  
		 return encodePassword;  
	 }  
	 
	 public static boolean isPasswordEquel(String password, String encodePassword) {  			
		 return passwordEncoder.matches(password.toString() , encodePassword);
	 }  
}
