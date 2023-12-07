package com.Ecommerce_Cusomer_App.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.OrderResponseDto;
import com.Ecommerce_Cusomer_App.dto.TransactionDetails;
import com.Ecommerce_Cusomer_App.entity.Cart;
import com.Ecommerce_Cusomer_App.entity.Orders;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.OrderRepository;
import com.Ecommerce_Cusomer_App.utils.Helper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	private static final String KEY = "";
	private static final String KEY_SECRET = "";
	private static final String CURRENCY = "INR";

	public ResponseEntity<CommonApiResponse> orderProductsFromCart(Long userId) {

		CommonApiResponse response = new CommonApiResponse();
		try {
			if (userId == 0) {
				response.setResponseMessage("user id missing");
				response.setSuccess(false);

				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			User user = this.userService.getUserById(userId);

			if (user == null) {
				response.setResponseMessage("User not found");
				response.setSuccess(false);

				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			List<Cart> carts = this.cartService.findByUser(user);

			if (CollectionUtils.isEmpty(carts)) {
				response.setResponseMessage("No Products found in Cart");
				response.setSuccess(false);

				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			String orderTime = String.valueOf(LocalDateTime.now());
			List<Orders> orders = new ArrayList<>();
			String orderId = Helper.generateOrderId(); // Generate a unique order ID for the admin

			for (Cart cart : carts) {
				Orders order = new Orders();
				order.setOrderId(orderId);
				order.setUser(user);
				order.setOrderTime(orderTime);
				order.setQuantity(cart.getQuantity());
				order.setSubCategory(cart.getSubCategory());
				order.setPrice(cart.getSubCategory().getPrice());
				orders.add(order);
			}

			List<Orders> addedOrders = orderRepository.saveAll(orders);

			System.out.println(addedOrders);

			if (CollectionUtils.isEmpty(addedOrders)) {
				response.setResponseMessage("Failed to Order Products");
				response.setSuccess(false);
				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			try {
				this.cartService.deleteCarts(carts);
			} catch (Exception e) {
				response.setResponseMessage("Failed to Order Products");
				response.setSuccess(false);
				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			response.setResponseMessage("Order Placed Successful");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<OrderResponseDto> fetchAllOrders() {

		OrderResponseDto response = new OrderResponseDto();

		try {
			List<Orders> orders = new ArrayList<>();

			orders = orderRepository.findAll();

			if (CollectionUtils.isEmpty(orders)) {
				response.setResponseMessage("No orders found");
				response.setSuccess(false);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
			}

			response.setOrders(orders);
			response.setTotalAmount(calulateTotalAmountFromCart(orders));
			response.setResponseMessage("Orders fetched successful");
			response.setSuccess(true);

			return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<OrderResponseDto> fetchUserOrders(Long userId) {
		OrderResponseDto response = new OrderResponseDto();

		try {
			if (userId == 0) {
				response.setResponseMessage("User Id missing");
				response.setSuccess(false);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.BAD_REQUEST);
			}

			User user = userService.getUserById(userId);

			if (user == null) {
				response.setResponseMessage("User not found, failed to fetch user orders");
				response.setSuccess(false);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.BAD_REQUEST);
			}

			List<Orders> orders = new ArrayList<>();

			orders = orderRepository.findByUser(user);

			if (CollectionUtils.isEmpty(orders)) {
				response.setResponseMessage("No orders found");
				response.setSuccess(false);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
			}

			response.setOrders(orders);
			response.setTotalAmount(calulateTotalAmountFromCart(orders));
			response.setResponseMessage("Orders fetched successful");
			response.setSuccess(true);

			return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<OrderResponseDto> fetchOrdersByOrderId(String orderId) {
		OrderResponseDto response = new OrderResponseDto();

		try {
			if (orderId == null) {
				response.setResponseMessage("Order Id missing");
				response.setSuccess(true);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.BAD_REQUEST);
			}

			List<Orders> orders = new ArrayList<>();

			orders = orderRepository.findByOrderId(orderId);

			if (CollectionUtils.isEmpty(orders)) {
				response.setResponseMessage("No orders found");
				response.setSuccess(false);

				return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
			}

			response.setOrders(orders);
			response.setTotalAmount(calulateTotalAmountFromCart(orders));
			response.setResponseMessage("Orders fetched successful");
			response.setSuccess(true);

			return new ResponseEntity<OrderResponseDto>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private BigDecimal calulateTotalAmountFromCart(List<Orders> orders) {

		BigDecimal totalAmount = BigDecimal.ZERO;

		if (CollectionUtils.isEmpty(orders)) {
			return totalAmount;
		}

		for (Orders order : orders) {

			BigDecimal Amount = order.getSubCategory().getPrice().multiply(new BigDecimal(order.getQuantity()));

			totalAmount = totalAmount.add(Amount);
		}

		return totalAmount;
	}

	public TransactionDetails createTransaction(Double amount) {

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("amount", (amount * 100));
			jsonObject.put("currency", CURRENCY);

			RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);
			Order order = razorpayClient.orders.create(jsonObject);

			System.out.println(order);

			return prepareTransactionDetails(order);
		} catch (RazorpayException e) {
			e.printStackTrace();
		}
		return null;
	}

	private TransactionDetails prepareTransactionDetails(Order order) {
		String orderId = order.get("id");
		String currency = order.get("currency");
		Integer amount = order.get("amount");

		TransactionDetails transactionDetails = new TransactionDetails(orderId, currency, amount);
		return transactionDetails;
	}

}
