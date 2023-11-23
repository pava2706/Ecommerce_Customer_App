package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.service.UserService;

@RestController
@RequestMapping("/api/customer")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<Object> signUp(@RequestBody User user) {

		return userService.signUP(user);
	}

	@PostMapping("/verify")
	public ResponseEntity<Object> verifyOTPForSignUp(@RequestParam String phoneNumber, @RequestParam String otp) {
		return userService.verifyOTP(phoneNumber, otp);
	}

	@PostMapping("/signin")
	public ResponseEntity<Object> verifyOTPForSignIn(@RequestParam String phoneNumber) {
		return userService.signIn(phoneNumber);
	}

}
