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
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.UserLoginResponse;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.UserRole;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;
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

	public ResponseEntity<CommonApiResponse> signUp(User user) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (user.getEmail() == null || user.getName() == null || user.getPhoneNumber() == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

				response.setResponseMessage(
						"OTP sent successfully to the registered phone number:- " + recipientPhoneNumber);
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);

			} else {
				response.setResponseMessage("User Is Already Exists, Try to login orelse Register With Different No");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method for generating OTP
	private String generateOTP() {
		int otp = (int) (Math.random() * 1000000);
		return String.format("%06d", otp);
	}

	// Method to verify OTP entered by the user for SignUp
	public ResponseEntity<CommonApiResponse> verifyOTPSignUp(String phoneNumber, String otpEntered) {

		CommonApiResponse response = new CommonApiResponse();

		try {
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				response.setResponseMessage("User not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User user = userOptional.get();
			if (user.getOtp() == null || !user.getOtp().equals(otpEntered)) {
				response.setResponseMessage("Invalid OTP");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			LocalDateTime currentDateTime = LocalDateTime.now();
			LocalDateTime otpExpiryTime = user.getExpiryTime();

			if (otpExpiryTime != null && otpExpiryTime.isBefore(currentDateTime)) {
				response.setResponseMessage("OTP has expired");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// OTP verification successful, clear OTP
			user.setOtp(null);
			user.setExpiryTime(null);
			User res = userRepository.save(user);
			if (res != null) {
				response.setResponseMessage("SignUp sucessful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setResponseMessage("Failed to Signup");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> signIn(String phoneNumber) {

		CommonApiResponse response = new CommonApiResponse();

		try {
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				response.setResponseMessage("User not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

			response.setResponseMessage(
					"OTP sent successfully to the registered phone number:- " + recipientPhoneNumber);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to verify OTP entered by the user for SignIn
	public ResponseEntity<UserLoginResponse> verifyOTPSignIn(String phoneNumber, String otpEntered) {
		UserLoginResponse response = new UserLoginResponse();
		try {
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				response.setResponseMessage("User not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User user = userOptional.get();
			if (user.getOtp() == null || !user.getOtp().equals(otpEntered)) {
				response.setResponseMessage("Invalid OTP");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			LocalDateTime currentDateTime = LocalDateTime.now();
			LocalDateTime otpExpiryTime = user.getExpiryTime();

			if (otpExpiryTime != null && otpExpiryTime.isBefore(currentDateTime)) {
				response.setResponseMessage("OTP has expired");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			String jwtToken = null;

			// OTP verification successful, clear OTP
			user.setOtp(null);
			user.setExpiryTime(null);
			userRepository.save(user);
			jwtToken = jwtUtils.generateToken(user.getPhoneNumber());

			// user is authenticated
			if (jwtToken != null) {
				response.setUser(user);
				response.setJwtToken(jwtToken);
				response.setResponseMessage("Logged in sucessful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

			else {
				response.setResponseMessage("Failed to login");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).get();

	}

	public User getUserByEmailAndStatus(String email, String status) {
		return userRepository.findByEmailAndStatus(email, status);
	}

	public ResponseEntity<CommonApiResponse> statusUpdate(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<User> user = userRepository.findById(id);

			if (user.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			User ad = user.get();
			if (ad.getStatus().contains(UserStatus.ACTIVE.value())) {
				ad.setStatus(UserStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(UserStatus.ACTIVE.value());
			}
			userRepository.save(ad);
			response.setResponseMessage("Status " + ad.getStatus() + " sucessfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> update(User user, Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (user.getEmail() == null || user.getName() == null || user.getPhoneNumber() == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User data = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (data == null) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			data.setEmail(user.getEmail());
			data.setName(user.getName());
			data.setPhoneNumber(user.getPhoneNumber());
			data.setRole(UserRole.ROLE_CUSTOMER.value());
			data.setStatus(UserStatus.ACTIVE.value());
			userRepository.save(data);

			response.setResponseMessage("User Updated successfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<UserLoginResponse> getUser(Long id) {
		UserLoginResponse response = new UserLoginResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (user == null) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setResponseMessage("User Fetched Sucessfully");
			response.setUser(user);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<UserLoginResponse> getAllUser() {
		UserLoginResponse response = new UserLoginResponse();
		try {
			List<User> users = userRepository.findByStatusIn(Arrays.asList(UserStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(users)) {
				response.setResponseMessage("No Users Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setResponseMessage("Users Fetched Sucessfully..");
			response.setSuccess(true);
			response.setUsers(users);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> deleteUser(Long id) {

		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (user == null) {
				response.setResponseMessage("No Users Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			user.setStatus(UserStatus.DEACTIVATED.value());
			User user2 = userRepository.save(user);
			if (user2.getStatus().contains(UserStatus.ACTIVE.value())) {

				response.setResponseMessage("Unable to Delete...");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			response.setResponseMessage("User Deleted Sucesfully...");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> deleteAllUser() {

		CommonApiResponse response = new CommonApiResponse();

		try {
			List<User> users = userRepository.findByStatusIn(Arrays.asList(UserStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(users)) {
				response.setResponseMessage("No Users found,Unable to delete");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			for (User user : users) {
				user.setStatus(UserStatus.DEACTIVATED.value());
			}
			userRepository.saveAll(users);

			response.setResponseMessage("All Users Deleted Sucessfully..");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public User findByIdAndStatus(Long long1, String status) {
		return userRepository.findByIdAndStatus(long1, status);
	}

	public ResponseEntity<CommonApiResponse> resendOtp(String phoneNumber) {

		CommonApiResponse response = new CommonApiResponse();
		try {
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				response.setResponseMessage("User not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			PhoneNumber recipientPhoneNumber = new PhoneNumber(phoneNumber);
			PhoneNumber senderPhoneNumber = new PhoneNumber(twilioConfig.getPhoneNumber());
			LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // Set expiry time (5 minutes from now)
			User data = userOptional.get();
			String otp = generateOTP(); // Generate OTP
			String otpMessage = "Dear Customer, Your One-Time Password is: " + otp + ". "
					+ "Thank you for Using Our Service.";
			data.setOtp(otp); // Set OTP in the user object
			data.setExpiryTime(expiryTime); // Set OTP expiry time in the user object
			Message.creator(recipientPhoneNumber, senderPhoneNumber, otpMessage).create(); // Send OTP via Twilio

			userRepository.save(data); // Save user with OTP and expiry time

			response.setResponseMessage(
					"OTP sent successfully to the registered phone number:- " + recipientPhoneNumber);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public User getUserByphoneNumberAndStatus(String phoneNumber, String status) {
		return userRepository.findUserByphoneNumberAndStatus(phoneNumber, status);
	}

	public User getUserById(Long userId) {
		return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE.value());
	}

}