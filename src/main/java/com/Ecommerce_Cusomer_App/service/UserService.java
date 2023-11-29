package com.Ecommerce_Cusomer_App.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Ecommerce_Cusomer_App.configuration.TwilioConfig;
import com.Ecommerce_Cusomer_App.dto.UserLoginResponse;
import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;
import com.Ecommerce_Cusomer_App.utils.JwtUtils;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private JwtUtils jwtUtils;

	// method for sending OTP

	public ResponseEntity<Object> signUp(User user) {
		try {
			PhoneNumber recipientPhoneNumber = new PhoneNumber(user.getPhoneNumber());
			PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
			LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // Set expiry time (5 minutes from now)

			String otp = generateOTP(); // Generate OTP
			String otpMessage = "Dear Customer, Your One-Time Password is: " + otp + ". "
					+ "Thank you for Using Our Service.";

			user.setOtp(otp); // Set OTP in the user object
			user.setExpiryTime(expiryTime); // Set OTP expiry time in the user object
			user.setRole("Admin");

			Message.creator(recipientPhoneNumber, senderPhoneNumber, otpMessage).create(); // Send OTP via Twilio

			userRepository.save(user); // Save user with OTP and expiry time

			return CustomerUtils.getResponseEntity("OTP sent successfully to the registered phone number",
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method for generating OTP
	private String generateOTP() {
		int otp = (int) (Math.random() * 1000000);
		return String.format("%06d", otp);
	}

	// Method to verify OTP entered by the user
	public ResponseEntity<Object> verifyOTP(String phoneNumber, String otpEntered) {
		try {
			System.out.println(phoneNumber);
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				return CustomerUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
			}

			User user = userOptional.get();
			if (user.getOtp() == null || !user.getOtp().equals(otpEntered)) {
				return CustomerUtils.getResponseEntity("Invalid OTP", HttpStatus.BAD_REQUEST);
			}

			LocalDateTime currentDateTime = LocalDateTime.now();
			LocalDateTime otpExpiryTime = user.getExpiryTime();

			if (otpExpiryTime != null && otpExpiryTime.isBefore(currentDateTime)) {
				return CustomerUtils.getResponseEntity("OTP has expired", HttpStatus.BAD_REQUEST);
			}

			String jwtToken = null;
			UserLoginResponse response = new UserLoginResponse();
			// OTP verification successful, clear OTP
			user.setOtp(null);
			userRepository.save(user);
			jwtToken = jwtUtils.generateToken(user.getEmail());

			// user is authenticated
			if (jwtToken != null) {
				response.setUser(user);
				response.setJwtToken(jwtToken);
				return new ResponseEntity<>("Logged in sucessful  /n" + response, HttpStatus.OK);
			}

			else {
				return new ResponseEntity<>("Failed to login", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> signIn(String phoneNumber) {
		try {
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				return CustomerUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
			}

			User user = userOptional.get();
			PhoneNumber recipientPhoneNumber = new PhoneNumber(phoneNumber);
			PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());

			LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // Set expiry time (5 minutes from now)

			String otp = generateOTP(); // Generate OTP
			String otpMessage = "Dear Customer, Your One-Time Password is: " + otp + ". "
					+ "Thank you for Using Our Service.";

			// Send OTP via Twilio
			Message.creator(recipientPhoneNumber, senderPhoneNumber, otpMessage).create();

			// Set new OTP, its expiry time, session token, and update signedIn status in
			// the user object
			user.setOtp(otp);
			user.setExpiryTime(expiryTime);

			userRepository.save(user); // Save user with new OTP, session token, and signedIn status

			return CustomerUtils.getResponseEntity("OTP sent successfully to the registered phone number",
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).get();

	}

	public User getUserByEmailAndStatus(String email, String status) {
		return userRepository.findByEmailAndStatus(email, status);
	}

	public ResponseEntity<Object> statusUpdate(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			Optional<User> user = userRepository.findById(id);

			if (user.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}

			User ad = user.get();
			if (ad.getStatus().contains(UserStatus.ACTIVE.value())) {
				ad.setStatus(UserStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(UserStatus.ACTIVE.value());
			}
			userRepository.save(ad);
			return new ResponseEntity<>("Status " + ad.getStatus() + " sucessfully", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
