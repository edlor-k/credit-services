package ru.creditservices.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.StatementEntity;

import java.util.Optional;
import java.util.UUID;

public interface StatementRepository extends JpaRepository<StatementEntity, UUID> {

    Optional<StatementEntity> findStatementEntityByClient(ClientEntity client);

    Optional<StatementEntity> findStatementEntityByStatementId(UUID statementId);
}
