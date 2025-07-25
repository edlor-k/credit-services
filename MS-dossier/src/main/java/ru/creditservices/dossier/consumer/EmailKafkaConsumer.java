package ru.creditservices.dossier.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.creditservices.dossier.dto.EmailMessageDto;
import ru.creditservices.dossier.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailKafkaConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = {
                    "${kafka.topic.finish-registration}",
                    "${kafka.topic.create-documents}",
                    "${kafka.topic.send-documents}",
                    "${kafka.topic.send-ses}",
                    "${kafka.topic.credit-issued}",
                    "${kafka.topic.statement-denied}"
            },
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenEmailEvents(@Payload EmailMessageDto message) {
        log.info("Получено сообщение по теме {} для email={}", message.getTheme(), message.getAddress());
        emailService.sendEmail(message);
    }
}
