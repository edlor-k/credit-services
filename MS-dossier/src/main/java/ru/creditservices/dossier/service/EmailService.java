package ru.creditservices.dossier.service;

import ru.creditservices.dossier.dto.EmailMessageDto;

public interface EmailService {
    void sendEmail(EmailMessageDto message);
}
