package com.Ecommerce_Cusomer_App.dto;

import lombok.Data;

@Data
public class CartRequestDto {

	private int id;

	private int userId;

	private int subCategoryId;

	private int quantity;

}
