package com.Ecommerce_Cusomer_App.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Ecommerce_Cusomer_App.dto.AdminLoginResponse;
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.repository.AdminRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.JwtUtils;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private JwtUtils jwtUtils;

	public void addAdmin(Admin user) {
		adminRepository.save(user);
	}

	public Admin getUserByEmailIdAndStatus(String string, String value) {
		return adminRepository.findByEmailAndStatus(string, value);
	}

	public ResponseEntity<AdminLoginResponse> signIn(String email, String password) {

		AdminLoginResponse response = new AdminLoginResponse();
		try {
			if (email == null || password == null) {
				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			String jwtToken = null;
			Admin user = null;

			user = this.adminRepository.findByEmailAndStatus(email, AdminStatus.ACTIVE.value());

			if (!user.getEmail().equals(email) || !user.getPassword().equals(password)) {
				response.setResponseMessage("Invalid email or password.");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			jwtToken = jwtUtils.generateToken(email);

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
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> statusUpdate(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<Admin> admin = adminRepository.findById(id);

			if (admin.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Admin ad = admin.get();
			if (ad.getStatus().contains(AdminStatus.ACTIVE.value())) {
				ad.setStatus(AdminStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(AdminStatus.ACTIVE.value());
			}
			adminRepository.save(ad);
			response.setResponseMessage("Status " + ad.getStatus() + " sucessfully");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<AdminLoginResponse> fetchById(Long id) {
		AdminLoginResponse response = new AdminLoginResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<Admin> admin = adminRepository.findById(id);

			if (admin.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Admin ad = admin.get();
			response.setUser(ad);
			response.setResponseMessage("User Fetched Sucessfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
