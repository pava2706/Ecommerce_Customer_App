package com.Ecommerce_Cusomer_App.dto;

import lombok.Data;

@Data
public class VerifyOTPRequest {

	private String phoneNumber;
	private String otp;
}
