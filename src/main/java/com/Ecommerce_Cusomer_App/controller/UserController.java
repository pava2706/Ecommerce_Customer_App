package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.UserLoginResponse;
import com.Ecommerce_Cusomer_App.dto.VerifyOTPRequest;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.service.UserService;

@RestController
@RequestMapping("/api/customer")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/get")
	public String home() {
		return "welcome to Restaurant_Management app";
	}

	@PostMapping("/signup")
	public ResponseEntity<CommonApiResponse> signUp(@RequestBody User user) {

		return userService.signUp(user);
	}

	@PostMapping("/verifyforsignup")
	public ResponseEntity<CommonApiResponse> verifyOTPForSignUp(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		return userService.verifyOTPSignUp(phoneNumber, otp);
	}

	@PostMapping("/resendotp")
	public ResponseEntity<CommonApiResponse> resendOtp(@RequestBody User user) {
		return userService.resendOtp(user.getPhoneNumber());
	}

	@PostMapping("/signin")
	public ResponseEntity<CommonApiResponse> verifyOTPForSignIn(@RequestBody User user) {
		return userService.signIn(user.getPhoneNumber());
	}

	@PostMapping("/verifyforsignin")
	public ResponseEntity<UserLoginResponse> verifyOTPForSignIn(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		return userService.verifyOTPSignIn(phoneNumber, otp);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<CommonApiResponse> update(@RequestBody User user, @PathVariable("id") Long id) {
		return userService.update(user, id);
	}

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<CommonApiResponse> statusUpdate(@PathVariable("id") Long id) {
		return userService.statusUpdate(id);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<UserLoginResponse> getUser(@PathVariable("id") Long id) {
		return userService.getUser(id);
	}

	@GetMapping("/getall")
	public ResponseEntity<UserLoginResponse> getAllUser() {
		return userService.getAllUser();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<CommonApiResponse> deleteUser(@PathVariable("id") Long id) {
		return userService.deleteUser(id);
	}

	@DeleteMapping("/deleteall")
	public ResponseEntity<CommonApiResponse> deleteAllUser() {
		return userService.deleteAllUser();
	}

}
