package ru.creditservices.deal.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
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

    @Value("${kafka.topic.finish-registration}")
    private String finishRegistrationTopic;
    @Value("${kafka.topic.create-documents}")
    private String createDocumentsTopic;
    @Value("${kafka.topic.send-documents}")
    private String sendDocumentsTopic;
    @Value("${kafka.topic.send-ses}")
    private String sendSesTopic;
    @Value("${kafka.topic.credit-issued}")
    private String creditIssuedTopic;
    @Value("${kafka.topic.statement-denied}")
    private String statementDeniedTopic;

    private final Map<EmailTheme, String> topicMap = new EnumMap<>(EmailTheme.class);

    @PostConstruct
    private void initTopicMap() {
        topicMap.put(EmailTheme.FINISH_REGISTRATION, finishRegistrationTopic);
        topicMap.put(EmailTheme.CREATE_DOCUMENTS, createDocumentsTopic);
        topicMap.put(EmailTheme.SEND_DOCUMENTS, sendDocumentsTopic);
        topicMap.put(EmailTheme.SEND_SES, sendSesTopic);
        topicMap.put(EmailTheme.CREDIT_ISSUED, creditIssuedTopic);
        topicMap.put(EmailTheme.STATEMENT_DENIED, statementDeniedTopic);
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
