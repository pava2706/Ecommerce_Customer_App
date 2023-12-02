package com.Ecommerce_Cusomer_App.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.LocationResponseDto;
import com.Ecommerce_Cusomer_App.entity.Location;
import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.repository.LocationRepository;
import com.Ecommerce_Cusomer_App.repository.UserRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.LocationStatus;

@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GeocodingService geocodingService;

	public ResponseEntity<CommonApiResponse> createLocationWithGeocoding(Location location, Long customerId) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (location == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<User> customer = userRepository.findById(customerId);
			if (customer.isEmpty()) {
				response.setResponseMessage("Customer is not present, cannot add location");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			double[] latLong = geocodingService
					.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());

			Location loc = new Location();
			loc.setStreet(location.getStreet());
			loc.setCity(location.getCity());
			loc.setCountry(location.getCountry());
			loc.setLatitude(latLong[0]);
			loc.setLongitude(latLong[1]);
			loc.setCustomer(customer.get());
			loc.setStatus(LocationStatus.ACTIVE.value());

			Location savedLocation = locationRepository.save(loc);
			response.setResponseMessage("Location Added successfully: " + savedLocation);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Get all locations
	public ResponseEntity<LocationResponseDto> getAllLocationsWithGeocoding() {
		LocationResponseDto response = new LocationResponseDto();
		try {
			List<Location> locations = locationRepository.findAll();

			if (CollectionUtils.isEmpty(locations)) {
				response.setResponseMessage("No locations found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			for (Location location : locations) {
				double[] latLong = geocodingService
						.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());
				location.setLatitude(latLong[0]);
				location.setLongitude(latLong[1]);
			}
			response.setLocations(locations);
			response.setSuccess(true);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Get location by ID
	public ResponseEntity<LocationResponseDto> getLocationByIdWithGeocoding(Long id) {
		LocationResponseDto response = new LocationResponseDto();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<Location> locationOptional = locationRepository.findById(id);
			if (locationOptional.isEmpty()) {
				response.setResponseMessage("location Not found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Location location = locationOptional.get();
			double[] latLong = geocodingService
					.getLatLong(location.getStreet() + ", " + location.getCity() + ", " + location.getCountry());
			location.setLatitude(latLong[0]);
			location.setLongitude(latLong[1]);

			response.setResponseMessage("location Fetched Sucessfully");
			response.setSuccess(true);
			response.setLocation(location);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Update location by ID
	public ResponseEntity<CommonApiResponse> updateLocationWithGeocoding(Long id, Location locationDetails) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0 || locationDetails == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<Location> locOptional = locationRepository.findById(id);
			if (locOptional.isEmpty()) {
				response.setResponseMessage("No location found, unable to update");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Location location = locOptional.get();
			double[] latLong = geocodingService.getLatLong(locationDetails.getStreet() + ", "
					+ locationDetails.getCity() + ", " + locationDetails.getCountry());
			location.setStreet(locationDetails.getStreet());
			location.setCity(locationDetails.getCity());
			location.setCountry(locationDetails.getCountry());
			location.setLatitude(latLong[0]);
			location.setLongitude(latLong[1]);

			Location updatedLocation = locationRepository.save(location);
			response.setResponseMessage("Location Updated successfully: " + updatedLocation);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Delete location by ID
	public ResponseEntity<CommonApiResponse> deleteLocationById(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<Location> locationOptional = locationRepository.findById(id);
			if (locationOptional.isEmpty()) {
				response.setResponseMessage("No location found, unable to delete");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Location location = locationOptional.get();
			location.setCustomer(null);
			locationRepository.save(location);
			locationRepository.delete(location);
			response.setResponseMessage("Location Deleted successfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> deleteAllLocations() {
		CommonApiResponse response = new CommonApiResponse();
		try {
			List<Location> locations = locationRepository.findAll();

			if (CollectionUtils.isEmpty(locations)) {
				response.setResponseMessage("No location found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			locationRepository.deleteAll(locations);

			response.setResponseMessage("All Locations Deleted successfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> statusUpdate(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<Location> loc = locationRepository.findById(id);

			if (loc.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Location ad = loc.get();
			if (ad.getStatus().contains(AdminStatus.ACTIVE.value())) {
				ad.setStatus(AdminStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(AdminStatus.ACTIVE.value());
			}
			locationRepository.save(ad);
			response.setResponseMessage("Status " + ad.getStatus() + " sucessfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
