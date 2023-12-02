package com.Ecommerce_Cusomer_App.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.Ecommerce_Cusomer_App.dto.CartRequestDto;
import com.Ecommerce_Cusomer_App.dto.CartResponseDto;
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.entity.Cart;
import com.Ecommerce_Cusomer_App.entity.SubCategory;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.CartRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class CartService {

	private final Logger LOG = LoggerFactory.getLogger(CartService.class);

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SubCategoryService subCategoryService;

	public ResponseEntity<CommonApiResponse> addToCart(CartRequestDto request) {

		LOG.info("Request received for add to cart");
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (request == null || request.getUserId() == 0 || request.getSubCategoryId() == 0) {
				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

			if (user == null) {
				response.setResponseMessage("Failed to add to cart, Customer not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

			}

			SubCategory sub = this.subCategoryService.getSubCategoryById(request.getSubCategoryId());

			if (sub == null) {

				response.setResponseMessage("Failed to add to cart, Subcategory not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Cart cart = new Cart();
			cart.setUser(user);
			cart.setSubCategory(sub);
			cart.setQuantity(request.getQuantity());
			cart.setAddedTime(String.valueOf(LocalDateTime.now()));

			Cart savedCart = this.cartRepository.save(cart);

			if (savedCart == null) {
				response.setResponseMessage("Failed to add to cart");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			response.setResponseMessage("Food Added to Cart Successful");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> updateCart(CartRequestDto request) {

		CommonApiResponse response = new CommonApiResponse();
		LOG.info("Request received for updating the cart");
		try {
			if (request == null || request.getId() == 0) {
				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

			if (user == null) {
				response.setResponseMessage("Unauthorized User to update the Cart");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Cart cart = this.cartRepository.getCartById(request.getId());

			if (cart == null) {
				response.setResponseMessage("Cart not found:(");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			if (request.getQuantity() == 0) {
				this.cartRepository.delete(cart);
			} else {
				cart.setQuantity(request.getQuantity());
				this.cartRepository.save(cart);
				response.setResponseMessage("User Cart Updated Successful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setResponseMessage("Failed to update the Cart");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<CommonApiResponse> deleteCart(CartRequestDto request) {

		LOG.info("Request received for deleting the cart");
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (request.getId() == 0) {

				response.setResponseMessage("Missing Input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

			if (user == null) {
				response.setResponseMessage("Unauthorized User to delete the Cart");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Cart cart = this.cartRepository.getCartById(request.getId());

			if (cart == null) {
				response.setResponseMessage("Cart not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			this.cartRepository.delete(cart);
			response.setResponseMessage("User Cart Deleted Successful");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<CartResponseDto> fetchUserCartDetails(int userId) {

		LOG.info("Request received for fetching the user cart");

		CartResponseDto response = new CartResponseDto();
		try {
			User user = this.userService.findByIdAndStatus(userId, UserStatus.ACTIVE.value());

			if (user == null) {

				response.setResponseMessage("Unauthorized User to fetch the Cart");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			List<Cart> carts = new ArrayList<>();

			carts = this.cartRepository.findByUser(user);

			if (carts == null) {
				response.setResponseMessage("No Foods found in Cart");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			response.setResponseMessage("User Cart Fetched Successful");
			response.setSuccess(true);
			response.setTotalCartAmount(calulateTotalAmountFromCart(carts));
			response.setCarts(carts);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseMessage("SOMETHING_WENT_WRONG");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private BigDecimal calulateTotalAmountFromCart(List<Cart> carts) {

		BigDecimal totalAmount = BigDecimal.ZERO;

		if (CollectionUtils.isEmpty(carts)) {
			return totalAmount;
		}

		for (Cart cart : carts) {

			BigDecimal cartAmount = cart.getSubCategory().getPrice().multiply(new BigDecimal(cart.getQuantity()));

			totalAmount = totalAmount.add(cartAmount);
		}

		return totalAmount;
	}

}
