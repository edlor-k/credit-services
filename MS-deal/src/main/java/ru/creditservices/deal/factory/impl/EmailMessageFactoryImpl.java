package ru.creditservices.deal.factory.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.impl.ClientLookupServiceImpl;
import ru.creditservices.deal.util.EmailTemplateConstants;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageFactoryImpl implements EmailMessageFactory {

    private final ClientLookupServiceImpl clientLookupService;

    @Override
    public EmailMessageDto buildEmailMessage(UUID statementId, EmailTheme theme) {
        log.debug("Building email message for statementId: {}, theme: {}", statementId, theme);

        String clientEmail = clientLookupService.getEmailByStatementId(statementId);
        if (clientEmail == null) {
            throw new ClientNotFoundException("Client email not found for statementId: " + statementId);
        }

        String text = EmailTemplateConstants.EMAIL_TEMPLATES.getOrDefault(
                theme,
                "Уведомление по вашей заявке"
        );

        EmailMessageDto emailMessage = EmailMessageDto.builder()
                .address(clientEmail)
                .theme(theme)
                .statementId(statementId)
                .text(text)
                .build();

        log.debug("Email message built: {}", emailMessage);
        return emailMessage;
    }
}
