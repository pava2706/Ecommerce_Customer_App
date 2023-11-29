package com.Ecommerce_Cusomer_App.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Ecommerce_Cusomer_App.dto.GeocodingResponse;
import com.Ecommerce_Cusomer_App.dto.GeocodingResponse.Result.Geometry.Location;

@Service
public class GeocodingService {

    @Value("${geocoding.api.key}")
    private String apiKey;

    public double[] getLatLong(String address) {
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?key=" + apiKey + "&address=" + address;

        RestTemplate restTemplate = new RestTemplate();
        GeocodingResponse response = restTemplate.getForObject(apiUrl, GeocodingResponse.class);

        if (response != null && response.getStatus().equals("OK") && response.getResults() != null && response.getResults().length > 0) {
            GeocodingResponse.Result result = response.getResults()[0];
            GeocodingResponse.Result.Geometry geometry = result.getGeometry();
            Location location = geometry.getLocation();
            return new double[]{location.getLat(), location.getLng()};
        }
 
        return new double[]{0, 0}; // Return default values if geocoding fails
    }
}



