package com.Ecommerce_Cusomer_App.dto;

import lombok.Data;

@Data
public class CartRequestDto {

	private Long id;

	private Long userId;

	private Long subCategoryId;

	private int quantity;

}
