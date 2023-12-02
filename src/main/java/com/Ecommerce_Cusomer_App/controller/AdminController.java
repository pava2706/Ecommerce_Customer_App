package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.dto.AdminLoginResponse;
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.service.AdminService;

@RestController
@RequestMapping("api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@PostMapping("/signin")
	public ResponseEntity<AdminLoginResponse> signIn(@RequestBody Admin admin) {
		return adminService.signIn(admin.getEmail(), admin.getPassword());
	}

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<CommonApiResponse> statusUpdate(@PathVariable("id") Long id) {
		return adminService.statusUpdate(id);
	}

	@GetMapping("/fetch/{id}")
	public ResponseEntity<AdminLoginResponse> fetchById(@PathVariable("id") Long id) {
		return adminService.fetchById(id);
	}

}
