package com.Ecommerce_Cusomer_App.dto;

import com.Ecommerce_Cusomer_App.entity.Admin;

import lombok.Data;

@Data
public class AdminLoginResponse extends CommonApiResponse {

	private Admin user;

	private String jwtToken;
}
