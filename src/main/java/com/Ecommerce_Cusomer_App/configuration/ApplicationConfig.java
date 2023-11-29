package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.Ecommerce_Cusomer_App.interceptor.RequestHeaderInterceptor;

// for interceptor
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

	@Autowired
	private RequestHeaderInterceptor requestHeaderInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestHeaderInterceptor);
	}
	
//	@Value("${google.maps.api.key}")
//    private String googleMapsApiKey;
//
//    @Bean
//    public GeoApiContext geoApiContext() {
//        return new GeoApiContext.Builder()
//                .apiKey(googleMapsApiKey)
//                .build();
//    }
}
