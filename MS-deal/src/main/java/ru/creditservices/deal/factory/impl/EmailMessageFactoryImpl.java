package ru.creditservices.deal.factory.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.impl.ClientLookupServiceImpl;
import ru.creditservices.deal.util.EmailTemplateProvider;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageFactoryImpl implements EmailMessageFactory {

    private final ClientLookupServiceImpl clientLookupService;
    private final EmailTemplateProvider emailTemplateProvider;

    @Override
    public EmailMessageDto buildEmailMessage(UUID statementId, EmailTheme theme, String info) {
        log.debug("Формирование email-сообщения: statementId={}, theme={}", statementId, theme);

        String clientEmail = clientLookupService.getEmailByStatementId(statementId);
        if (Objects.isNull(clientEmail)) {
            log.error("Email клиента не найден: statementId={}", statementId);
            throw new ClientNotFoundException("Не найден email клиента по заявке: " + statementId);
        }

        String template = emailTemplateProvider.getTemplate(theme);
        String text = switch (theme) {
            case SEND_SES -> String.format(template, "SesCode: " + info);
            default -> template != null ? template : "Уведомление по вашей заявке";
        };

        EmailMessageDto message = EmailMessageDto.builder()
                .address(clientEmail)
                .theme(theme)
                .statementId(statementId)
                .text(text)
                .build();

        log.debug("Email-сообщение сформировано: {}", message);
        return message;
    }
}
