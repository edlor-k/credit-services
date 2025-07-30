package ru.creditservices.statement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "external.deal")
public class DealServiceProperties {
    private String baseUrl;
    private String statementPath;
    private String offerPath;
}
