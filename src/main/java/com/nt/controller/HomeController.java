package com.nt.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HomeController {

	@GetMapping()
	public String getHome() {
		return "Welcome to Spring Security";
	}
	
	
	
	
	@GetMapping("/csrf")
	public CsrfToken getCsrfToken(HttpServletRequest request) 
	{
		/*
		 	This method will get you the csrf token that you can use it for post/ patch/ delete
		 	request.
		 */
		
		return (CsrfToken) request.getAttribute("_csrf");
	}
}
