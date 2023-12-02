package com.Ecommerce_Cusomer_App.dto;

import java.util.ArrayList;
import java.util.List;

import com.Ecommerce_Cusomer_App.entity.Location;

import lombok.Data;

@Data
public class LocationResponseDto extends CommonApiResponse {

	private List<Location> locations = new ArrayList<>();
	
	private Location location;
}
