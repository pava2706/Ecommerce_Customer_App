package com.Ecommerce_Cusomer_App.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Ecommerce_Cusomer_App.configuration.TwilioConfig;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TwilioConfig twilioConfig;

	// method for sending OTP
	public ResponseEntity<Object> signUP(User user) {
		try {
			PhoneNumber recipentPhoneNumber = new PhoneNumber(user.getPhoneNumber());
			PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
			String otp = generateOTP();
			String otpMessage = "Dear Customer, Youe One_Time Password(otp) is: " + otp + ". "
					+ "Thank you for Using Our Service.";
			user.setOtp(otp);
			Message.creator(recipentPhoneNumber, senderPhoneNumber, otpMessage).create();
			userRepository.save(user);
			return CustomerUtils.getResponseEntity("otp send sucessfully to user Regsitered phone Number",
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
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				return CustomerUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
			}
			User user = userOptional.get();
			if (user.getOtp().equals(otpEntered)) {
				user.setOtp(null); // Clear OTP after verification
				userRepository.save(user);
				return CustomerUtils.getResponseEntity("OTP verified successfully", HttpStatus.OK);
			} else {
				return CustomerUtils.getResponseEntity("Invalid OTP", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> signIn(String phoneNumber) {
		try {
			User userOptional = userRepository.findByPhoneNumber(phoneNumber).get();
			if (userOptional.getPhoneNumber().equals(phoneNumber)) {
				PhoneNumber recipentPhoneNumber = new PhoneNumber(phoneNumber);
				PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
				String otp = generateOTP();
				String otpMessage = "Dear Customer, Youe One_Time Password(otp) is: " + otp + ". "
						+ "Thank you for Using Our Service.";
				userOptional.setOtp(otp);
				Message.creator(recipentPhoneNumber, senderPhoneNumber, otpMessage).create();
				userRepository.save(userOptional);
				return CustomerUtils.getResponseEntity("otp send sucessfully to user Regsitered phone Number",
						HttpStatus.OK);
			} else {
				return CustomerUtils.getResponseEntity("Invalid PhoneNUmber", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).get();

	}

}
