package ru.creditservices.dossier.service;

import java.util.UUID;

public interface DealRestService {
    void updateStatementStatus(UUID statementId, String status);
}
