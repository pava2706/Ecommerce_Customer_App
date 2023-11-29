package com.Ecommerce_Cusomer_App.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce_Cusomer_App.entity.Location;
import com.Ecommerce_Cusomer_App.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

	private final LocationService locationService;

	public LocationController(LocationService locationService) {
		this.locationService = locationService;
	}

	// Create a location
	@PostMapping("/add/{customerid}")
	public ResponseEntity<Object> createLocation(@ModelAttribute Location location,@PathVariable("customerid") Long customerid) {
		return locationService.createLocation(location,customerid);

	} 

	// Get all locations
	@GetMapping
	public ResponseEntity<Object> getAllLocations() {
		return locationService.getAllLocations();

	}

	// Get location by ID
	@GetMapping("/{id}")
	public ResponseEntity<Object> getLocationById(@PathVariable Long id) {
		return locationService.getLocationById(id);

	}

	// Update location by ID
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateLocation(@PathVariable Long id, @RequestBody Location locationDetails) {
		return locationService.updateLocation(id, locationDetails);

	}

	// Delete location by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteLocation(@PathVariable Long id) {
		return locationService.deleteLocation(id);

	}

}
