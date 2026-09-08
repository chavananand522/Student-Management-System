package com.itvedant.StudentManagement.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

	public UserRepository usersReposatory;
	
	public UserServiceImpl(UserRepository usersReposatory) {
		this.usersReposatory=usersReposatory;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users users = usersReposatory.findByUserName(username)
		        .orElseThrow(() -> new UsernameNotFoundException("Invalid Username"));
		
		return User.withUsername(username).password(users.getPassword()).disabled(!users.isActive()).build();
	}

}
