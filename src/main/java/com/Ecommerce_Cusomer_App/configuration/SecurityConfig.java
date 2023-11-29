package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.Ecommerce_Cusomer_App.filter.JwtAuthFilter;
import com.Ecommerce_Cusomer_App.utils.Constants.UserRole;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthFilter authFilter;

	@Bean
	// authentication
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}
	
	

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())

				.authorizeHttpRequests(auth -> auth

//						// this APIs are only accessible by ADMIN
//						.requestMatchers("/api/user/admin/register","/api/user/delete/restaurant", "/api/order/fetch/all",
//								"/api/category/update", "/api/category/add", "/api/category/delete")
//						.hasAuthority(UserRole.ROLE_ADMIN.value())
//						 

						// this APIs are only accessible by CUSTOMER
						.requestMatchers("/api/user/login", "/api/user/register", "/api/order/add",
								"/api/order/fetch/user-wise", "/api/cart/update", "/api/cart/add", "/api/cart/fetch",
								"/api/cart/delete", "/api/food/review/add")
						.hasAuthority(UserRole.ROLE_CUSTOMER.value())

						.anyRequest().permitAll())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
