package ru.creditservices.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.creditservices.deal.config.CalculatorProperties;

@SpringBootApplication
@EnableConfigurationProperties(CalculatorProperties.class)
public class MsDealApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDealApplication.class, args);
    }

}
