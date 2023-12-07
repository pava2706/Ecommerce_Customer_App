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
import com.Ecommerce_Cusomer_App.dto.CartResponseDto;
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.service.CartService;

@RestController
@RequestMapping("api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

//	@PreAuthorize("hasRole('Customer')")
	@PostMapping("/add")

	public ResponseEntity<CommonApiResponse> addCategory(@RequestBody CartRequestDto request) {
		return cartService.addToCart(request);
	}

	@PutMapping("/update")

	public ResponseEntity<CommonApiResponse> updateCart(@RequestBody CartRequestDto request) {
		return cartService.updateCart(request);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<CommonApiResponse> deleteCart(@RequestBody CartRequestDto request) {
		return cartService.deleteCart(request);
	}

	@GetMapping("/fetch/{id}")

	public ResponseEntity<CartResponseDto> fetchUserCart(@PathVariable("id") Long userId) {
		return cartService.fetchUserCartDetails(userId);
	}

}