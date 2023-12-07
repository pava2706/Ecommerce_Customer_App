package com.Ecommerce_Cusomer_App.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.Ecommerce_Cusomer_App.entity.Orders;

import lombok.Data;

@Data
public class OrderResponseDto extends CommonApiResponse {

	private List<Orders> orders = new ArrayList<>();

	private BigDecimal totalAmount = BigDecimal.ZERO;
}
