package com.nt.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nt.entity.User;
import com.nt.repo.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManager authenticationManager;
    
    private final JwtService jwtService;

	public UserService(UserRepository userRepository, 
					   BCryptPasswordEncoder bCryptPasswordEncoder, 
					   AuthenticationManager authenticationManager,
					   JwtService jwtService)
	{
		super();
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

    public User register(User user) {
    	user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    	return userRepository.save(user);
    }
	
    
    public String verify(User user) {
        Authentication authenticate
                = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword()
                )
        );

        if(authenticate.isAuthenticated())
            return jwtService.generateToken(user);
        return "failure";
    }
    
    
    

}
