package com.Ecommerce_Cusomer_App.dto;

import java.util.ArrayList;
import java.util.List;

import com.Ecommerce_Cusomer_App.entity.Category;

import lombok.Data;

@Data
public class CategoryResponseDto {

	private List<Category> categories = new ArrayList<>();
	
	private Category category;
}
