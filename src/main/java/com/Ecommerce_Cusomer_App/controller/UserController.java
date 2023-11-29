package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.dto.VerifyOTPRequest;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.service.UserService;

@RestController
@RequestMapping("/api/customer")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<Object> signUp(@RequestBody User user) {

		return userService.signUp(user);
	}

	@PostMapping("/verify")
	public ResponseEntity<Object> verifyOTPForSignUp(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		System.out.println(phoneNumber + " " + otp);
		return userService.verifyOTP(phoneNumber, otp);
	}

	@PostMapping("/signin")
	public ResponseEntity<Object> verifyOTPForSignIn(@RequestBody User user) {
		return userService.signIn(user.getPhoneNumber());
	}

//	@PostMapping("/signOut")
//	public ResponseEntity<Object> signOut(@RequestBody User user) {
//		return userService.signOut(user.getPhoneNumber());
//	}

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<Object> statusUpdate(@PathVariable("id") Long id) {
		return userService.statusUpdate(id);
	}

}
