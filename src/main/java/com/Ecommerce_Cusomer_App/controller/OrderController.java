package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.OrderResponseDto;
import com.Ecommerce_Cusomer_App.dto.TransactionDetails;
import com.Ecommerce_Cusomer_App.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired

	private OrderService orderService;

	@PostMapping("/add")
	public ResponseEntity<CommonApiResponse> placeOrder(@RequestParam("userId") Long userId) {
		return orderService.orderProductsFromCart(userId);
	}

	// Method TO fetch all orders

	@GetMapping("/fetch/all")
	public ResponseEntity<OrderResponseDto> fetchAllOrders() {
		return orderService.fetchAllOrders();
	}

	// Method to fetch user orders

	@GetMapping("/fetch/user-wise")
	public ResponseEntity<OrderResponseDto> fetchUserOrders(@RequestParam("userId") Long userId) {
		return orderService.fetchUserOrders(userId);
	}

	// Method to fetch orders by order id

	@GetMapping("/fetch")
	public ResponseEntity<OrderResponseDto> fetchOrdersByOrderId(@RequestParam("orderId") String orderId) {
		return orderService.fetchOrdersByOrderId(orderId);
	}

	// Method to create Transaction

	@GetMapping("/createTransaction/{amount}")
	public TransactionDetails createTransaction(@PathVariable("amount") Double amount) {
		return orderService.createTransaction(amount);
	}
}
