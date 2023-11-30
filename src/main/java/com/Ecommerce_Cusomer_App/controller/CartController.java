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

import com.Ecommerce_Cusomer_App.dto.CartRequestDto;
import com.Ecommerce_Cusomer_App.service.CartService;

@RestController
@RequestMapping("api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping("/add")

	public ResponseEntity<Object> addCategory(@RequestBody CartRequestDto request) {
		return cartService.addToCart(request);
	}
	
	@PutMapping("/update") 
	
	public ResponseEntity<Object> updateCart(@RequestBody CartRequestDto request) {
		return cartService.updateCart(request);
	}
	 
	@DeleteMapping("/delete")
	public ResponseEntity<Object> deleteCart(@RequestBody CartRequestDto request) {
		return cartService.deleteCart(request);
	}
	
	@GetMapping("/fetch/{id}")
	
	public ResponseEntity<Object> fetchUserCart(@PathVariable("id") int userId) {
		return cartService.fetchUserCartDetails(userId);
	}

}