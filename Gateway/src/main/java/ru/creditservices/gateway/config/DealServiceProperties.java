package ru.creditservices.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "external.deal")
public class DealServiceProperties {
    private String baseUrl;
    private String statementPath;
    private String offerPath;
    private String calculatePath;
    private String createDocumentsPath;
    private String signDocumentsPath;
    private String verifyDocumentsPath;
    private String adminGetStatementPath;
    private String adminGetAllStatementsPath;
}
