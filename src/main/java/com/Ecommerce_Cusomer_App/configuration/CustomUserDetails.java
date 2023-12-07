package com.Ecommerce_Cusomer_App.configuration;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.Ecommerce_Cusomer_App.entity.User;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String phoneNumber;
	private String otp;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(User user) {
		this.phoneNumber = user.getPhoneNumber();
		this.otp = user.getOtp(); // Fetch OTP from User

		// Assuming getRole() returns a single role for the user
		String role = user.getRole();
		this.authorities = Collections.singleton(new SimpleGrantedAuthority(role));
	}

	// Implementing UserDetails methods
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null; // Assuming no password stored or used for OTP-based authentication
	}

	@Override
	public String getUsername() {
		return phoneNumber;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// Getter and setter for OTP
	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
}
