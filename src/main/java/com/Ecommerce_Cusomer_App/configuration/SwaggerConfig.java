package com.Ecommerce_Cusomer_App.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

	// for swagger UI use --> http://localhost:8090/swagger-ui/index.html

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("ECommerce Customer App Application")
						.description("ECommerce Customer App Application using Spring Boot 3").version("v0.0.1")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.externalDocs(new ExternalDocumentation().description("Code With pavan"));
	}

}


