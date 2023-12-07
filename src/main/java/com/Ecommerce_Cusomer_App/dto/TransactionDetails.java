package com.Ecommerce_Cusomer_App.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {

	private String orderId;
	private String currency;
	private Integer amount;

}
