package ru.creditservices.gateway.service;

import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.StatementDto;

import java.util.List;
import java.util.UUID;

public interface DealClientService {
    void finishRegistration(FinishRegistrationRequestDto request, UUID statementId);
    void createDocuments(UUID statementId);
    void signDocuments(UUID statementId);
    void verifyDocuments(UUID statementId, String sesCode);
    List<StatementDto> fetchStatements();
    StatementDto fetchStatementById(UUID statementId);
}
