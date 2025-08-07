package ru.creditservices.deal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicProperties {
    private Map<String, String> topics;
}
