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

	public ResponseEntity<Object> addToCart(CartRequestDto request) {

		LOG.info("Request received for add to cart");

		try {
			if (request == null || request.getUserId() == 0 || request.getSubCategoryId() == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}

			User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

			if (user == null) {
				return new ResponseEntity<>("Failed to add to cart, Customer not found", HttpStatus.BAD_REQUEST);
			}

			SubCategory sub = this.subCategoryService.getSubCategoryById(request.getSubCategoryId());

			if (sub == null) {

				return new ResponseEntity<>("Failed to add to cart, Subcategory not found", HttpStatus.BAD_REQUEST);
			}

			Cart cart = new Cart();
			cart.setUser(user);
			cart.setSubCategory(sub);
			cart.setQuantity(request.getQuantity());
			cart.setAddedTime(
					String.valueOf(LocalDateTime.now()));

			Cart savedCart = this.cartRepository.save(cart);

			if (savedCart == null) {
				return new ResponseEntity<>("Failed to add to cart", HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<>("Food Added to Cart Successful", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> updateCart(CartRequestDto request) {

		LOG.info("Request received for updating the cart");

		if (request == null || request.getId() == 0) {
			return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
		}

		User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

		if (user == null) {
			return new ResponseEntity<>("Unauthorized User to update the Cart", HttpStatus.BAD_REQUEST);
		}

		Cart cart = this.cartRepository.getCartById(request.getId());

		if (cart == null) {
			return new ResponseEntity<>("Cart not found:(", HttpStatus.BAD_REQUEST);
		}

		try {
			if (request.getQuantity() == 0) {
				this.cartRepository.delete(cart);
			} else {
				cart.setQuantity(request.getQuantity());

				this.cartRepository.save(cart);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Failed to update the Cart", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("User Cart Updated Successful", HttpStatus.OK);

	}

	public ResponseEntity<Object> deleteCart(CartRequestDto request) {

		LOG.info("Request received for deleting the cart");

		if (request.getId() == 0) {

			return new ResponseEntity<>("cart id is missing", HttpStatus.BAD_REQUEST);
		}

		User user = this.userService.findByIdAndStatus(request.getUserId(), UserStatus.ACTIVE.value());

		if (user == null) {

			return new ResponseEntity<>("Unauthorized User to delete the Cart", HttpStatus.BAD_REQUEST);
		}

		Cart cart = this.cartRepository.getCartById(request.getId());

		if (cart == null) {
			return new ResponseEntity<>("Cart not found", HttpStatus.BAD_REQUEST);
		}

		try {
			this.cartRepository.delete(cart);
		} catch (Exception e) {
			return new ResponseEntity<>("Failed to Delete the Cart", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("User Cart Deleted Successful", HttpStatus.OK);
	}

	public ResponseEntity<Object> fetchUserCartDetails(int userId) {

		LOG.info("Request received for fetching the user cart");

		CartResponseDto response = new CartResponseDto();

		User user = this.userService.findByIdAndStatus(userId, UserStatus.ACTIVE.value());

		if (user == null) {

			return new ResponseEntity<>("Unauthorized User to fetch the Cart", HttpStatus.BAD_REQUEST);
		}

		List<Cart> carts = new ArrayList<>();

		carts = this.cartRepository.findByUser(user);

		if (carts == null) {
			return new ResponseEntity<>("No Foods found in Cart", HttpStatus.BAD_REQUEST);
		}

		response.setTotalCartAmount(calulateTotalAmountFromCart(carts));
		response.setCarts(carts);
		return new ResponseEntity<>("User Cart Fetched Successful" + response, HttpStatus.OK);
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
