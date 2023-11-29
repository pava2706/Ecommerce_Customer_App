package com.Ecommerce_Cusomer_App.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CustomerUtils {

	private CustomerUtils() {

	}

	public static ResponseEntity<Object> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
		return new ResponseEntity<Object>(responseMessage, httpStatus);
	}

	public static ResponseEntity<byte[]> getImageResponseEntity(byte[] imageBytes, MediaType mediaType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType); // Set the provided media type (e.g., MediaType.IMAGE_JPEG)

		return ResponseEntity.ok().headers(headers).body(imageBytes);
	}
}
