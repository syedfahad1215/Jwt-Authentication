package com.nt.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.entity.User;
import com.nt.repo.UserRepository;
import com.nt.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	
	
	
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}



	@PostMapping("/register")
	public User saveUser(@RequestBody User user) {
		return userService.register(user);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody User user) {
		
		return userService.verify(user);
	}

}
