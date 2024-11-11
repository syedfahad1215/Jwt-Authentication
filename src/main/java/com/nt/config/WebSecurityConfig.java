package com.nt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	
	private final UserDetailsService userDetailsService;
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	
	public WebSecurityConfig(UserDetailsService userDetailsService, 
							 JwtAuthenticationFilter jwtAuthenticationFilter) 
	{
		super();
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
	{
		
		http
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests( req -> 
				req
				.requestMatchers(HttpMethod.POST,"/user/register").permitAll()
				.requestMatchers(HttpMethod.POST ,"/user/login").permitAll()
				.anyRequest()
				.authenticated()
		)
		.httpBasic(Customizer.withDefaults())
		/*
		  The below line will add the custom JwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter,
		  because the jwtAuthenticationFilter will authenticate the user based on the token which is generated by 
		  jwt. If the token is correct then it will send the success message to the UsernamePasswordAuthenticationFilter
		  and tell that you don't bother about this user, he is authenticated. As the User is Authenticated the request will
		  be moving forword to another filter which is after the jwtAuthenticationFilter.
		 */
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
		
	}

	
	@Bean
	public AuthenticationProvider authenticationProvider() 
	{
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(bCryptPasswordEncoder());
		return provider;
	}
	
	
	 @Bean
	 public AuthenticationManager authenticationManager(
	            		AuthenticationConfiguration configuration ) throws Exception 
	 {
	        return configuration.getAuthenticationManager();
	  }
	
	
	@Bean 
	public BCryptPasswordEncoder bCryptPasswordEncoder() 
	{
		return new BCryptPasswordEncoder();
	}
}

