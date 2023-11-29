package com.Ecommerce_Cusomer_App.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.Ecommerce_Cusomer_App.entity.Admin;

public class CustomAdminDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	private List<GrantedAuthority> authorities;

	public CustomAdminDetails(Admin user) {
		email = user.getEmail();
		password = user.getPassword();
		authorities = new ArrayList<>();

		String role = user.getRole();

		authorities.add(new SimpleGrantedAuthority(role));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return email;
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

	@Override
	public String getPassword() {
		return password;
	}

}
