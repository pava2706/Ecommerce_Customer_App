package com.Ecommerce_Cusomer_App.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.Ecommerce_Cusomer_App.configuration.TwilioConfig;
import com.Ecommerce_Cusomer_App.dto.UserLoginResponse;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.CategoryStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.UserRole;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;
import com.Ecommerce_Cusomer_App.utils.JwtUtils;
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
			if (user.getEmail() == null || user.getName() == null || user.getPhoneNumber() == null) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<User> userOptional = userRepository.findByPhoneNumber(user.getPhoneNumber());

			if (userOptional.isEmpty()) {
				PhoneNumber recipientPhoneNumber = new PhoneNumber(user.getPhoneNumber());
				PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
				LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // Set expiry time (5 minutes from now)
 
				String otp = generateOTP(); // Generate OTP
				String otpMessage = "Dear Customer, Your One-Time Password is: " + otp + ". "
						+ "Thank you for Using Our Service.";

				user.setOtp(otp); // Set OTP in the user object
				user.setExpiryTime(expiryTime); // Set OTP expiry time in the user object
				user.setRole(UserRole.ROLE_CUSTOMER.value());
				user.setStatus(UserStatus.ACTIVE.value());

				Message.creator(recipientPhoneNumber, senderPhoneNumber, otpMessage).create(); // Send OTP via Twilio

				userRepository.save(user); // Save user with OTP and expiry time

				return CustomerUtils.getResponseEntity(
						"OTP sent successfully to the registered phone number:- " + recipientPhoneNumber,
						HttpStatus.OK);
			} else {
				return CustomerUtils.getResponseEntity(
						"User Is Already Registered with us, Try to Activate the Account orelse Register With Different No",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
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

	// Method to verify OTP entered by the user for SignUp
	public ResponseEntity<Object> verifyOTPSignUp(String phoneNumber, String otpEntered) {
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

			// OTP verification successful, clear OTP
			user.setOtp(null);
			user.setExpiryTime(null);
			User res = userRepository.save(user);
			if (res != null) {
				return new ResponseEntity<>("SignUp sucessful", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Failed to Signup", HttpStatus.BAD_REQUEST);
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

	// Method to verify OTP entered by the user for SignIn
	public ResponseEntity<Object> verifyOTPSignIn(String phoneNumber, String otpEntered) {
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
			user.setExpiryTime(null);
			userRepository.save(user);
			jwtToken = jwtUtils.generateToken(user.getEmail());

			// user is authenticated
			if (jwtToken != null) {
				response.setUser(user);
				response.setJwtToken(jwtToken);
				return new ResponseEntity<>("Logged in sucessful" + response, HttpStatus.OK);
			}

			else {
				return new ResponseEntity<>("Failed to login", HttpStatus.BAD_REQUEST);
			}
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

	public ResponseEntity<Object> update(User user, Long id) {
		try {
			if (user.getEmail() == null || user.getName() == null || user.getPhoneNumber() == null) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}

			Optional<User> user2 = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (user2.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}
			User data = user2.get();
			if (!data.getPhoneNumber().contains(user.getPhoneNumber())) {
				PhoneNumber recipientPhoneNumber = new PhoneNumber(user.getPhoneNumber());
				PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
				LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // Set expiry time (5 minutes from now)

				String otp = generateOTP(); // Generate OTP
				String otpMessage = "Dear Customer, Your One-Time Password is: " + otp + ". "
						+ "Thank you for Using Our Service.";
				data.setEmail(user.getEmail());
				data.setName(user.getName());
				data.setPhoneNumber(user.getPhoneNumber());
				data.setRole(UserRole.ROLE_CUSTOMER.value());
				data.setStatus(data.getStatus());
				data.setOtp(otp); // Set OTP in the user object
				data.setExpiryTime(expiryTime); // Set OTP expiry time in the user object
				Message.creator(recipientPhoneNumber, senderPhoneNumber, otpMessage).create(); // Send OTP via Twilio

				userRepository.save(data); // Save user with OTP and expiry time

				return CustomerUtils.getResponseEntity(
						"OTP sent successfully to the registered phone number:- " + recipientPhoneNumber,
						HttpStatus.OK);
			} else {
				data.setEmail(user.getEmail());
				data.setName(user.getName());
				data.setPhoneNumber(user.getPhoneNumber());
				data.setRole(UserRole.ROLE_CUSTOMER.value());
				data.setStatus(UserStatus.ACTIVE.value());
				userRepository.save(data); // Save user with OTP and expiry time

				return CustomerUtils.getResponseEntity("User Updated successfully", HttpStatus.OK);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> verifyOTPForUpdate(String phoneNumber, String otpEntered) {
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

			// OTP verification successful, clear OTP
			user.setOtp(null);
			user.setExpiryTime(null);
			User res = userRepository.save(user);
			if (res != null) {
				return new ResponseEntity<>("User Updated sucessful", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Failed to Update", HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> getUser(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<User> user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (user.isEmpty()) {
				return CustomerUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
			}
			return CustomerUtils.getResponseEntity("" + user.get(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> getAllUser() {
		try {
			List<User> users = userRepository.findByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(users)) {
				return CustomerUtils.getResponseEntity("No Users found", HttpStatus.BAD_REQUEST);
			}
			return CustomerUtils.getResponseEntity("" + users, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> deleteUser(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<User> user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (user.isEmpty()) {
				return CustomerUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
			}
			user.get().setStatus(UserStatus.DEACTIVATED.value());
			User user2 = userRepository.save(user.get());
			if (user2.getStatus().contains(UserStatus.ACTIVE.value())) {
				return CustomerUtils.getResponseEntity("Unable to Delete...", HttpStatus.BAD_REQUEST);
			}
			return CustomerUtils.getResponseEntity("User Deleted Sucesfully...", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> deleteAllUser() {
		try {
			List<User> users = userRepository.findByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(users)) {
				return CustomerUtils.getResponseEntity("No Users found,Unable to delete", HttpStatus.BAD_REQUEST);
			}
			for (User user : users) {
				user.setStatus(UserStatus.DEACTIVATED.value());
			}
			userRepository.saveAll(users);

			return CustomerUtils.getResponseEntity("All Users Deleted Sucessfully.." + users, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

}