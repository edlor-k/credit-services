package ru.creditservices.deal.service;

import java.util.UUID;

public interface DocumentsService {
    void documentsSigned(UUID statementId, String sesCode);
    void requestToSignDocuments(UUID statementId);
    void updateStatementStatus(UUID statementId, String status);
    void sendFinishRegistrationRequest(UUID statementId);
    void sendCreateDocumentsRequest(UUID statementId);
    void sendDocumentsSendInfo(UUID statementId);
}
