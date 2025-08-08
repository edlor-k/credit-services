package ru.creditservices.deal.service;

import java.util.UUID;

public interface DocumentsService {
    void prepareDocumentsForSigning(UUID statementId);
    void documentsSigned(UUID statementId, String sesCode);
    void requestToSignDocuments(UUID statementId);
    void updateStatementStatus(UUID statementId, String status);
    void finishRegistrationInfo(UUID statementId);
}
