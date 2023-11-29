package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce_Cusomer_App.entity.Location;
import com.Ecommerce_Cusomer_App.service.LocationService;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/add/{customerId}")
    public ResponseEntity<Object> createLocation(@RequestBody Location location, @PathVariable("customerId") Long customerId) {
        return locationService.createLocationWithGeocoding(location, customerId);
    }

    @GetMapping("getall")
    public ResponseEntity<Object> getAllLocations() {
        return locationService.getAllLocationsWithGeocoding();
    }

    @GetMapping("get/{id}")
    public ResponseEntity<Object> getLocationById(@PathVariable Long id) {
        return locationService.getLocationByIdWithGeocoding(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Object> updateLocation(@PathVariable Long id, @RequestBody Location locationDetails) {
        return locationService.updateLocationWithGeocoding(id, locationDetails);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteLocation(@PathVariable Long id) {
        return locationService.deleteLocationById(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllLocations() {
        return locationService.deleteAllLocations();
    }
}

