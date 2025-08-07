package ru.creditservices.deal.factory;

import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.model.enums.EmailTheme;

import java.util.UUID;

public interface EmailMessageFactory {
    EmailMessageDto buildEmailMessage(UUID statementId, EmailTheme theme);
}
