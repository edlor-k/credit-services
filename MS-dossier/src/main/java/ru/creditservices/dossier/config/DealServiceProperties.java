package ru.creditservices.dossier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "external.deal")
public class DealServiceProperties {
    private String baseUrl;
    private String putPath;
}
