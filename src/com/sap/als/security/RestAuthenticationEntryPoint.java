package com.sap.als.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint{

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException arg2) throws IOException, ServletException {
		//WWW-Authenticate: Basic realm="fake"
		response.addHeader("WWW-Authenticate", "Basic realm=\"fake\"");
		response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
		
		
	}
 
}
