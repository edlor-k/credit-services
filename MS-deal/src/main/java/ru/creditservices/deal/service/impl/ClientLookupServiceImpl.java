package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.service.ClientLookupService;
import ru.creditservices.deal.service.StatementManagerService;

import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientLookupServiceImpl implements ClientLookupService {

    private final StatementManagerService statementManagerService;

    @Override
    public String getEmailByStatementId(UUID statementId) {
        log.debug("Processing client lookup request for statementId={}", statementId);
        StatementEntity statement = statementManagerService.getStatementOrThrow(statementId);
        ClientEntity client = Optional.ofNullable(statement)
            .map(StatementEntity::getClient)
            .orElseThrow(() -> {
                log.error("No client found for statementId={}", statementId);
                return new ClientNotFoundException("Client not found for statementId: " + statementId);
            });
        String email = client.getEmail();
        log.debug("Found email={} for statementId={}", email, statementId);
        return email;
    }
}
