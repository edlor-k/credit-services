package ru.creditservices.deal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "external.calculator")
public class CalculatorServiceProperties {
    private String baseUrl;
    private String offersPath;
    private String calcPath;
}
