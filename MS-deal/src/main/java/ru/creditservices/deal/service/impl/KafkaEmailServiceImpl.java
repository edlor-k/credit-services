package ru.creditservices.deal.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.config.KafkaTopicProperties;
import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.exception.KafkaTopicNotFoundException;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.KafkaEmailService;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEmailServiceImpl implements KafkaEmailService {

    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    private final Map<EmailTheme, String> topicMap = new EnumMap<>(EmailTheme.class);

    @PostConstruct
    private void initTopiMap() {
        topicMap.put(EmailTheme.FINISH_REGISTRATION, kafkaTopicProperties.getTopics().get("finish-registration"));
        topicMap.put(EmailTheme.CREATE_DOCUMENTS, kafkaTopicProperties.getTopics().get("create-documents"));
        topicMap.put(EmailTheme.SEND_DOCUMENTS, kafkaTopicProperties.getTopics().get("send-documents"));
        topicMap.put(EmailTheme.SEND_SES, kafkaTopicProperties.getTopics().get("send-ses"));
        topicMap.put(EmailTheme.CREDIT_ISSUED, kafkaTopicProperties.getTopics().get("credit-issued"));
        topicMap.put(EmailTheme.STATEMENT_DENIED, kafkaTopicProperties.getTopics().get("statement-denied"));
    }

    @Override
    public void sendMessage(EmailMessageDto emailMessageDto) {
        log.debug("Preparing to send message for theme: {}", emailMessageDto.getTheme());
        String topic = topicMap.get(emailMessageDto.getTheme());
        if (topic == null) {
            log.error("No topic found for email theme: {}", emailMessageDto.getTheme());
            throw new KafkaTopicNotFoundException("No topic found for email theme: " + emailMessageDto.getTheme());
        }
        log.info("Sending message to topic: {}", topic);
        kafkaTemplate.send(topic, emailMessageDto);
        log.debug("Message sent: {}", emailMessageDto);
    }
}
