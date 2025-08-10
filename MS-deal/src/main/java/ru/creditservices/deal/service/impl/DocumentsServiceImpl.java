package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.exception.InvalidSesCodeException;
import ru.creditservices.deal.exception.SesCodeNotFoundException;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.DocumentsService;
import ru.creditservices.deal.service.KafkaEmailService;
import ru.creditservices.deal.service.StatementManagerService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentsServiceImpl implements DocumentsService {
    private final StatementManagerService statementManagerService;
    private final EmailMessageFactory emailMessageFactory;
    private final KafkaEmailService kafkaEmailService;

    @Override
    public void sendFinishRegistrationRequest(UUID statementId) {
        log.info("Sending finish registration info for statementId: {}", statementId);
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.FINISH_REGISTRATION, null);
        kafkaEmailService.sendMessage(message);
    }

    @Override
    public void sendCreateDocumentsRequest(UUID statementId) {
        log.info("Sending create documents for statementId: {}", statementId);
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.CREATE_DOCUMENTS, null);
        kafkaEmailService.sendMessage(message);
    }

    @Override
    public void sendDocumentsSendInfo(UUID statementId) {
        log.info("Sending document send info for statementId: {}", statementId);
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.SEND_DOCUMENTS, null);
        kafkaEmailService.sendMessage(message);
    }

    @Override
    public void requestToSignDocuments(UUID statementId) {
        String sesCode = statementManagerService.generateSesCode(statementId);
        log.debug("Requesting to sign documents for statementId: {}, SES Code: {}", statementId, sesCode);
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.SEND_SES, sesCode);
        kafkaEmailService.sendMessage(message);
    }

    @Override
    public void documentsSigned(UUID statementId, String sesCode) {
        StatementEntity statement = statementManagerService.getStatementOrThrow(statementId);
        String sesCodeFromDb = Optional.ofNullable(statement)
                .map(StatementEntity::getSesCode)
                .orElseThrow(() -> {
                    log.error("No sesCode found for statementId={}", statementId);
                    return new SesCodeNotFoundException("SesCode not found for statementId: " + statementId);
                });
        if (!sesCode.equals(sesCodeFromDb)) {
            log.error("Invalid SES code for statementId={}, expected={}, received={}",
                    statementId, sesCodeFromDb, sesCode);
            throw new InvalidSesCodeException("Invalid SES code for statementId: " + statementId);
        }
        statementManagerService.documentsSigned(statementId);
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.CREDIT_ISSUED, null);
        kafkaEmailService.sendMessage(message);
        log.info("Documents signed for statementId: {}", statementId);
    }

    @Override
    public void updateStatementStatus(UUID statementId, String status) {
        log.info("Updating status for statementId: {}, new status: {}", statementId, status);
        statementManagerService.updateStatementStatus(statementId, status);
        log.debug("Status updated for statementId: {}", statementId);
    }


}
