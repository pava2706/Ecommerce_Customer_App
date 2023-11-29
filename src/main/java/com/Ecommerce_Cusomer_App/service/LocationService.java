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

	public LocationService(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	// Create a location
	public ResponseEntity<Object> createLocation(Location location, Long customerid) {

		try {
			if (location == null) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<User> customer = userRepository.findById(customerid);

			if (customer.isEmpty()) {
				return new ResponseEntity<Object>("Customer is not present u can't add location",
						HttpStatus.BAD_REQUEST);
			}

			Location loc = new Location();
			loc.setAddress(location.getAddress());
			loc.setLatitude(location.getLatitude());
			loc.setLongitude(location.getLongitude());
			loc.setCustomer(customer.get());
			loc.setStatus(LocationStatus.ACTIVE.value());

			Location data = locationRepository.save(loc);
			return CustomerUtils.getResponseEntity("Location Added successfully :-------->" + data, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Get all locations
	public ResponseEntity<Object> getAllLocations() {
		try {
			List<Location> locations = locationRepository.findAll();

			LocationResponseDto response = new LocationResponseDto();

			if (CollectionUtils.isEmpty(locations)) {

				return new ResponseEntity<Object>("No locations found", HttpStatus.NOT_FOUND);
			}
			response.setLocation(locations);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Get location by ID
	public ResponseEntity<Object> getLocationById(Long id) {

		try {
			if (id == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<Location> location = locationRepository.findById(id);
			if (location.isEmpty()) {
				return new ResponseEntity<Object>("No locations found", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Object>(location.get(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Update location by ID
	public ResponseEntity<Object> updateLocation(Long id, Location locationDetails) {
		try {

			if (id == 0 || locationDetails == null) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<Location> loc = locationRepository.findById(id);
			if (loc.isEmpty()) {
				return new ResponseEntity<Object>("No location found,U can't update it", HttpStatus.NOT_FOUND);
			}
			Location location = loc.get();
			location.setAddress(locationDetails.getAddress());
			location.setLatitude(locationDetails.getLatitude());
			location.setLongitude(locationDetails.getLongitude());
			location.setStatus(location.getStatus());
			Location data = locationRepository.save(location);
			return CustomerUtils.getResponseEntity("Location Updated successfully :-------->" + data, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Delete location by ID
	public ResponseEntity<Object> deleteLocation(Long id) {
		try {

			if (id == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}

			Optional<Location> loc = locationRepository.findById(id);
			if (loc.isEmpty()) {
				return new ResponseEntity<Object>("No location found,Unable to delete", HttpStatus.NOT_FOUND);
			}
			Location location = loc.get();
			location.setCustomer(null);
			locationRepository.save(location);
			locationRepository.delete(location);
			return CustomerUtils.getResponseEntity("Location Deleted successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
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
