package ru.creditservices.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "external.statement")
public class StatementServiceProperties {
    private String baseUrl;
    private String statementPath;
    private String offerPath;
}
