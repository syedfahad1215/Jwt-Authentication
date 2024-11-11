package com.nt.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nt.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	
	private final UserDetailsService userDetailsService;
	
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		super();
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		
		logger.info("Collecting the authorization header");
		
		if(authHeader == null || !authHeader.startsWith("Bearer")) {
			logger.info("Authorization header is null or Authorization header does'nt start with Bearer");
			filterChain.doFilter(request, response);
			return;
		}
		
		final String jwt = authHeader.substring(7);
		
		logger.info("Fetching the JWT token from the Authorization Header= "+jwt);
		
		final String username = jwtService.extractUserName(jwt);
		
		logger.info("Fetching the Username from JWT token= "+username);
		
		/*
		 Here we will get the Authentication object from  SecurityContextHolder, 
		 which holds authentication details for the current session.
	
		 */
		Authentication authentication = SecurityContextHolder
										.getContext()
										.getAuthentication();
		
		logger.info("Creating the Authentication object from SecurityContextHolder");
		
		if(username != null && authentication ==null ) 
		{
			logger.info("Username, Authentication is not equals to null");
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			logger.info("Fetching the UserDetails from UserDetailsService where user= "+userDetails);
			
			/*
			 The validation  verify's the token's signature, expiration, or other criteria.
			 */
			if(jwtService.isTokenValid(jwt, userDetails)) 
			{
				
				logger.info("Jwt token is valid");
				
				/*
				  creates a UsernamePasswordAuthenticationToken, representing an authenticated user.
				 */
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				        userDetails,
				        null,
				        userDetails.getAuthorities()
				);
				
				logger.info("Creating the UsernamePasswordAuthenticationToken Object");
				
				/*
				 Adds additional details about the request (such as IP address and session information)
				 to the authenticationToken
				 */
				authenticationToken.setDetails(
				        new WebAuthenticationDetailsSource().buildDetails(request)
				);
				
				logger.info("Setting the WebAuthenticationDetailsSource object UsernamePasswordAuthenticationToken Object");
				/*
				 Stores the authenticationToken in the SecurityContext, making the 
				 authenticated user available for the current request and subsequent 
				 components in the filter chain.
				 */
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				
				logger.info("Setting the AuthenticationToken to SecurityContextHolder");
			}
			
		}
		
		filterChain.doFilter(request, response);
		
	}

}
