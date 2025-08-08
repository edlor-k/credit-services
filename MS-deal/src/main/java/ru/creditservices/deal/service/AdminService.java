package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.StatementDto;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    List<StatementDto> getAllStatements();
    StatementDto getStatementById(UUID statementId);
}
