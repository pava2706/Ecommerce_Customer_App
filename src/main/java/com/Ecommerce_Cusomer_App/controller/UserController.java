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

	@PostMapping("/verifyforsignup")
	public ResponseEntity<Object> verifyOTPForSignUp(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		return userService.verifyOTPSignUp(phoneNumber, otp);
	}

	@PostMapping("/signin")
	public ResponseEntity<Object> verifyOTPForSignIn(@RequestBody User user) {
		return userService.signIn(user.getPhoneNumber());
	}

	@PostMapping("/verifyforsignin")
	public ResponseEntity<Object> verifyOTPForSignIn(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		return userService.verifyOTPSignIn(phoneNumber, otp);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Object> update(@RequestBody User user, @PathVariable("id") Long id) {
		return userService.update(user, id);
	}

	@PostMapping("/verifyforupdate")
	public ResponseEntity<Object> verifyOTPForUpdate(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		String otp = verifyOTPRequest.getOtp();
		return userService.verifyOTPForUpdate(phoneNumber, otp);
	}

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<Object> statusUpdate(@PathVariable("id") Long id) {
		return userService.statusUpdate(id);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
		return userService.getUser(id);
	}

	@GetMapping("/getall")
	public ResponseEntity<Object> getAllUser() {
		return userService.getAllUser();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) {
		return userService.deleteUser(id);
	}

	@DeleteMapping("/deleteall")
	public ResponseEntity<Object> deleteAllUser() {
		return userService.deleteAllUser();
	}

}
