package com.nt.service;

import java.util.Objects;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nt.CustomUserDetails;
import com.nt.entity.User;
import com.nt.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;


	public CustomUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		
		if(Objects.isNull(user)) {
			System.out.println("User not Available");
			throw new UsernameNotFoundException("User not found exception");
		}
		
		return new CustomUserDetails(user);
	}

	
}
