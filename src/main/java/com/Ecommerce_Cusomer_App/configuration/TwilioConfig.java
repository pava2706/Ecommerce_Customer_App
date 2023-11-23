package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix="twilio")
public class TwilioConfig {
private String AccountSid;
private String AuthToken;
private String phoneNumber;
}
