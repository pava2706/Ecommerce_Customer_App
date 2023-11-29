package com.Ecommerce_Cusomer_App.dto;

import java.util.ArrayList;
import java.util.List;

import com.Ecommerce_Cusomer_App.entity.Location;

import lombok.Data;

@Data
public class LocationResponseDto {

	private List<Location> location = new ArrayList<>();
}
