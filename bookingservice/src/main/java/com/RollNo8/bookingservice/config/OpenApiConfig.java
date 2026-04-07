package com.RollNo8.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceApi() {

        return new OpenAPI().info(new io.swagger.v3.oas.models.info.Info()
                .title("Booking Service API - Title")
                .description("Booking Service API for RollNo8 - Description")
                .version("v1.0.0")
                .termsOfService("You break it, you buy it")
        );

    }
}
