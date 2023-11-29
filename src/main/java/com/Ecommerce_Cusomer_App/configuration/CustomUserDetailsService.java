package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.service.UserService;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;



@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = this.userService.getUserByEmailAndStatus(email, UserStatus.ACTIVE.value());

		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails; 

	}
}
