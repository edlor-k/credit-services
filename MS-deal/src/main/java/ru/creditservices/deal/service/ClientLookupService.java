package ru.creditservices.deal.service;

import java.util.UUID;

public interface ClientLookupService {
    String getEmailByStatementId(UUID statementId);
}
