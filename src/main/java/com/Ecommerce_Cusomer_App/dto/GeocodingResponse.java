package com.Ecommerce_Cusomer_App.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {
    private String status;
    private Result[] results;

    @Data
    @NoArgsConstructor 
    @AllArgsConstructor
    public static class Result {
        private Geometry geometry;

        @Data
        @NoArgsConstructor 
        @AllArgsConstructor
        public static class Geometry {
            private Location location;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Location {
                private double lat;
                private double lng;
            }
        }
    }
}


