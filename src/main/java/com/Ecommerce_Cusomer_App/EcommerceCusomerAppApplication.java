package com.Ecommerce_Cusomer_App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.Ecommerce_Cusomer_App.configuration.TwilioConfig;
import com.twilio.Twilio;

@SpringBootApplication
public class EcommerceCusomerAppApplication {

	@Autowired
	private TwilioConfig twilioConfig;
	
	@jakarta.annotation.PostConstruct
	public void setUp() {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(EcommerceCusomerAppApplication.class, args);
	}

}
