package ru.creditservices.calculator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Credit Services Calculator API",
                version = "0.8.1",
                description = "API for calculating loan offers and credit scoring"
        )
)
@SpringBootApplication
public class MsCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCalculatorApplication.class, args);
    }

}
