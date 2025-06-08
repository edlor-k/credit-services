package ru.creditservices.calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Credit Services Calculator API",
                version = "0.8",
                description = "API for calculating loan offers and credit scoring"
        )
)
public class OpenApiConfig {
}
