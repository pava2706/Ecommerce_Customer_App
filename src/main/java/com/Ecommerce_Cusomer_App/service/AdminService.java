package com.Ecommerce_Cusomer_App.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Ecommerce_Cusomer_App.dto.AdminLoginResponse;
import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.repository.AdminRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;
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

	public ResponseEntity<Object> signIn(String email, String password) {
		try {

			AdminLoginResponse response = new AdminLoginResponse();

			if (email == null || password == null) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}

			String jwtToken = null;
			Admin user = null;

			user = this.adminRepository.findByEmailAndStatus(email, AdminStatus.ACTIVE.value());
			
			if (!user.getEmail().equals(email) || !user.getPassword().equals(password)) {
				return new ResponseEntity<>("Invalid email or password.", HttpStatus.BAD_REQUEST);
			}

			jwtToken = jwtUtils.generateToken(email);

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

	public ResponseEntity<Object> statusUpdate(Long id) {
		try {
			if(id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			Optional<Admin> admin=adminRepository.findById(id);
			
			if(admin.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}
			
			Admin ad=admin.get();
			if(ad.getStatus().contains(AdminStatus.ACTIVE.value())) {
				ad.setStatus(AdminStatus.DEACTIVATED.value());
			}
			else {
				ad.setStatus(AdminStatus.ACTIVE.value());
			}
			adminRepository.save(ad);
			return new ResponseEntity<>("Status "+ad.getStatus()+" sucessfully", HttpStatus.OK);

		}
		 catch (Exception e) {
				e.printStackTrace();
			}
			return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	public ResponseEntity<Object> fetchById(Long id) {
		try {
			if(id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			Optional<Admin> admin=adminRepository.findById(id);
			
			if(admin.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}
			
			Admin ad=admin.get();
			return new ResponseEntity<>(ad, HttpStatus.OK);

		}
		 catch (Exception e) {
				e.printStackTrace();
			}
			return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
		
	}


}
