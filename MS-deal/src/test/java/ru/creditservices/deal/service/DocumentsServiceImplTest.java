package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.exception.InvalidSesCodeException;
import ru.creditservices.deal.exception.SesCodeNotFoundException;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.service.impl.DocumentsServiceImpl;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentsServiceImplTest {

    @Mock private StatementManagerService statementManagerService;
    @Mock private EmailMessageFactory emailMessageFactory;
    @Mock private KafkaEmailService kafkaEmailService;

    @InjectMocks
    private DocumentsServiceImpl documentsService;

    @Test
    @DisplayName("sendFinishRegistrationRequest — отправляет сообщение с темой FINISH_REGISTRATION")
    void sendFinishRegistrationRequest_sendsKafka() {
        UUID id = UUID.randomUUID();

        documentsService.sendFinishRegistrationRequest(id);

        verify(emailMessageFactory).buildEmailMessage(eq(id), eq(EmailTheme.FINISH_REGISTRATION), isNull());
        verify(kafkaEmailService).sendMessage(any());
        verifyNoMoreInteractions(emailMessageFactory, kafkaEmailService, statementManagerService);
    }

    @Test
    @DisplayName("sendCreateDocumentsRequest — отправляет сообщение с темой CREATE_DOCUMENTS")
    void sendCreateDocumentsRequest_sendsKafka() {
        UUID id = UUID.randomUUID();

        documentsService.sendCreateDocumentsRequest(id);

        verify(emailMessageFactory).buildEmailMessage(eq(id), eq(EmailTheme.CREATE_DOCUMENTS), isNull());
        verify(kafkaEmailService).sendMessage(any());
        verifyNoMoreInteractions(emailMessageFactory, kafkaEmailService, statementManagerService);
    }

    @Test
    @DisplayName("sendDocumentsSendInfo — отправляет сообщение с темой SEND_DOCUMENTS")
    void sendDocumentsSendInfo_sendsKafka() {
        UUID id = UUID.randomUUID();

        documentsService.sendDocumentsSendInfo(id);

        verify(emailMessageFactory).buildEmailMessage(eq(id), eq(EmailTheme.SEND_DOCUMENTS), isNull());
        verify(kafkaEmailService).sendMessage(any());
        verifyNoMoreInteractions(emailMessageFactory, kafkaEmailService, statementManagerService);
    }

    @Test
    @DisplayName("requestToSignDocuments — генерирует SES, шлёт SEND_SES с кодом")
    void requestToSignDocuments_generatesSesAndSends() {
        UUID id = UUID.randomUUID();
        when(statementManagerService.generateSesCode(id)).thenReturn("ABC123");

        documentsService.requestToSignDocuments(id);

        InOrder inOrder = inOrder(statementManagerService, emailMessageFactory, kafkaEmailService);
        inOrder.verify(statementManagerService).generateSesCode(id);
        inOrder.verify(emailMessageFactory).buildEmailMessage(eq(id), eq(EmailTheme.SEND_SES), eq("ABC123"));
        inOrder.verify(kafkaEmailService).sendMessage(any());
        verifyNoMoreInteractions(statementManagerService, emailMessageFactory, kafkaEmailService);
    }

    @Test
    @DisplayName("documentsSigned — OK: код совпадает, обновляет статус и шлёт CREDIT_ISSUED")
    void documentsSigned_ok_updatesAndSends() {
        UUID id = UUID.randomUUID();
        StatementEntity statement = mock(StatementEntity.class);
        when(statementManagerService.getStatementOrThrow(id)).thenReturn(statement);
        when(statement.getSesCode()).thenReturn("CODE");

        documentsService.documentsSigned(id, "CODE");

        InOrder inOrder = inOrder(statementManagerService, emailMessageFactory, kafkaEmailService, statement);
        inOrder.verify(statementManagerService).getStatementOrThrow(id);
        inOrder.verify(statementManagerService).documentsSigned(id);
        inOrder.verify(emailMessageFactory).buildEmailMessage(eq(id), eq(EmailTheme.CREDIT_ISSUED), isNull());
        inOrder.verify(kafkaEmailService).sendMessage(any());
        verifyNoMoreInteractions(statementManagerService, emailMessageFactory, kafkaEmailService, statement);
    }

    @Test
    @DisplayName("documentsSigned — бросает SesCodeNotFoundException, если код в заявке отсутствует")
    void documentsSigned_throwsWhenSesMissing() {
        UUID id = UUID.randomUUID();
        StatementEntity statement = mock(StatementEntity.class);
        when(statementManagerService.getStatementOrThrow(id)).thenReturn(statement);
        when(statement.getSesCode()).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertThrows(
                SesCodeNotFoundException.class,
                () -> documentsService.documentsSigned(id, "ANY")
        );

        verify(statementManagerService).getStatementOrThrow(id);
        verify(statement).getSesCode();
        verifyNoMoreInteractions(statementManagerService, statement);
        verifyNoInteractions(emailMessageFactory, kafkaEmailService);
    }

    @Test
    @DisplayName("documentsSigned — бросает InvalidSesCodeException, если код не совпадает")
    void documentsSigned_throwsWhenSesInvalid() {
        UUID id = UUID.randomUUID();
        StatementEntity statement = mock(StatementEntity.class);
        when(statementManagerService.getStatementOrThrow(id)).thenReturn(statement);
        when(statement.getSesCode()).thenReturn("EXPECTED");

        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidSesCodeException.class,
                () -> documentsService.documentsSigned(id, "WRONG")
        );

        verify(statementManagerService).getStatementOrThrow(id);
        verify(statement).getSesCode();
        verifyNoMoreInteractions(statementManagerService, statement);
        verifyNoInteractions(emailMessageFactory, kafkaEmailService);
    }

    @Test
    @DisplayName("updateStatementStatus — делегирует в StatementManagerService")
    void updateStatementStatus_delegates() {
        UUID id = UUID.randomUUID();
        String status = "DOCUMENT_SIGNED";

        documentsService.updateStatementStatus(id, status);

        verify(statementManagerService).updateStatementStatus(id, status);
        verifyNoMoreInteractions(statementManagerService);
        verifyNoInteractions(emailMessageFactory, kafkaEmailService);
    }
}
