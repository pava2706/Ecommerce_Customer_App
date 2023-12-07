package com.Ecommerce_Cusomer_App.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private SubCategory subCategory;

	private int quantity;

	private String orderTime;
	
	 private BigDecimal price;
	 
	 public BigDecimal getTotalAmount() {
	        BigDecimal total = BigDecimal.ZERO;
	        if (price != null) {
	            total = price.multiply(BigDecimal.valueOf(quantity));
	        } 
	        return total;
	    }
}
