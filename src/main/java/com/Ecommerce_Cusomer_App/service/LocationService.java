package com.Ecommerce_Cusomer_App.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.Ecommerce_Cusomer_App.dto.LocationResponseDto;
import com.Ecommerce_Cusomer_App.entity.Location;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.LocationRepository;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.LocationStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;

@Service
public class LocationService {
	
	@Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeocodingService geocodingService;

    public ResponseEntity<Object> createLocationWithGeocoding(Location location, Long customerId) {
        try {
            if (location == null) {
                return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
            }

            Optional<User> customer = userRepository.findById(customerId);
            if (customer.isEmpty()) {
                return new ResponseEntity<>("Customer is not present, cannot add location", HttpStatus.BAD_REQUEST);
            }

            double[] latLong = geocodingService.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());

            Location loc = new Location();
            loc.setStreet(location.getStreet());
            loc.setCity(location.getCity());
            loc.setCountry(location.getCountry());
            loc.setLatitude(latLong[0]);
            loc.setLongitude(latLong[1]);
            loc.setCustomer(customer.get());
            loc.setStatus(LocationStatus.ACTIVE.value());

            Location savedLocation = locationRepository.save(loc);
            return ResponseEntity.ok("Location Added successfully: " + savedLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }


	// Get all locations
    public ResponseEntity<Object> getAllLocationsWithGeocoding() {
        try {
            List<Location> locations = locationRepository.findAll();

            if (CollectionUtils.isEmpty(locations)) {
                return new ResponseEntity<>("No locations found", HttpStatus.NOT_FOUND);
            }

            for (Location location : locations) {
                double[] latLong = geocodingService.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());
                location.setLatitude(latLong[0]);
                location.setLongitude(latLong[1]);
            }

            LocationResponseDto response = new LocationResponseDto();
            response.setLocation(locations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }


	// Get location by ID
    public ResponseEntity<Object> getLocationByIdWithGeocoding(Long id) {
        try {
            if (id == 0) {
                return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
            }

            Optional<Location> locationOptional = locationRepository.findById(id);
            if (locationOptional.isEmpty()) {
                return new ResponseEntity<>("No locations found", HttpStatus.NOT_FOUND);
            }

            Location location = locationOptional.get();
            double[] latLong = geocodingService.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());
            location.setLatitude(latLong[0]);
            location.setLongitude(latLong[1]);

            return ResponseEntity.ok(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }


	// Update location by ID
    public ResponseEntity<Object> updateLocationWithGeocoding(Long id, Location locationDetails) {
        try {
            if (id == 0 || locationDetails == null) {
                return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
            }

            Optional<Location> locOptional = locationRepository.findById(id);
            if (locOptional.isEmpty()) {
                return new ResponseEntity<>("No location found, unable to update", HttpStatus.NOT_FOUND);
            }

            Location location = locOptional.get();
            double[] latLong = geocodingService.getLatLong(locationDetails.getStreet() + ", " + locationDetails.getCity() + ", " + locationDetails.getCountry());
            location.setStreet(locationDetails.getStreet());
            location.setCity(locationDetails.getCity());
            location.setCountry(locationDetails.getCountry());
            location.setLatitude(latLong[0]);
            location.setLongitude(latLong[1]);

            Location updatedLocation = locationRepository.save(location);
            return ResponseEntity.ok("Location Updated successfully: " + updatedLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }


	// Delete location by ID
    public ResponseEntity<Object> deleteLocationById(Long id) {
        try {
            if (id == 0) {
                return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
            }

            Optional<Location> locationOptional = locationRepository.findById(id);
            if (locationOptional.isEmpty()) {
                return new ResponseEntity<>("No location found, unable to delete", HttpStatus.NOT_FOUND);
            }

            Location location = locationOptional.get();
            location.setCustomer(null);
            locationRepository.save(location);
            locationRepository.delete(location);

            return ResponseEntity.ok("Location Deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }

    
    public ResponseEntity<Object> deleteAllLocations() {
        try {
            List<Location> locations = locationRepository.findAll();

            if (CollectionUtils.isEmpty(locations)) {
                return new ResponseEntity<>("No locations found", HttpStatus.NOT_FOUND);
            }

            locationRepository.deleteAll(locations);

            return ResponseEntity.ok("All Locations Deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SOMETHING_WENT_WRONG");
    }


	public ResponseEntity<Object> statusUpdate(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			Optional<Location> loc = locationRepository.findById(id);

			if (loc.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}

			Location ad = loc.get();
			if (ad.getStatus().contains(AdminStatus.ACTIVE.value())) {
				ad.setStatus(AdminStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(AdminStatus.ACTIVE.value());
			}
			locationRepository.save(ad);
			return new ResponseEntity<>("Status " + ad.getStatus() + " sucessfully", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
