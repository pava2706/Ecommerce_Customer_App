package com.Ecommerce_Cusomer_App.dto;

import com.Ecommerce_Cusomer_App.entity.User;

import lombok.Data;

@Data
public class UserLoginResponse {

	private User user;

	private String jwtToken;
}
