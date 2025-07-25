package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.EmailMessageDto;

public interface KafkaEmailService {
    void sendMessage(EmailMessageDto emailMessageDto);
}
