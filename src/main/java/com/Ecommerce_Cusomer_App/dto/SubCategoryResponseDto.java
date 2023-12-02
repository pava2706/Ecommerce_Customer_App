package com.Ecommerce_Cusomer_App.dto;

import java.util.ArrayList;
import java.util.List;

import com.Ecommerce_Cusomer_App.entity.SubCategory;

import lombok.Data;

@Data
public class SubCategoryResponseDto extends CommonApiResponse {

	private List<SubCategory> categories = new ArrayList<>();

}
