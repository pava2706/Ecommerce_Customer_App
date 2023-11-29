package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.service.AdminService;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;

@Component
public class CustomAdminDetailsService implements UserDetailsService {

	@Autowired
	private AdminService adminService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Admin user = this.adminService.getUserByEmailIdAndStatus(email, UserStatus.ACTIVE.value());

		CustomAdminDetails customUserDetails = new CustomAdminDetails(user);

		return customUserDetails;

	}
}
