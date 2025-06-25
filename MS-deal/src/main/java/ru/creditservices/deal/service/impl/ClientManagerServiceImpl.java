package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.exception.ClientAlreadyExistException;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.LoanStatementEntity;
import ru.creditservices.deal.model.entity.ScoringDataEntity;
import ru.creditservices.deal.model.jsonb.PassportId;
import ru.creditservices.deal.repository.ClientRepository;
import ru.creditservices.deal.service.ClientManagerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientManagerServiceImpl implements ClientManagerService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ClientEntity createClientFromLoanStatementRequest(LoanStatementEntity entity) {
        boolean exists = clientRepository.findClientEntityByPassportId(
                entity.getPassportSeries(), entity.getPassportNumber()
        ).isPresent();

        if (exists) {
            log.warn("Client with passport {} already exists, returning existing client.",
                     buildPassportId(entity));
            throw new ClientAlreadyExistException(
                    "Client with passport " + buildPassportId(entity) + " already exists.");
        }
        log.info("Creating new client from loan statement request: {}", entity);
        return createInitialClient(entity);
    }

    private ClientEntity createInitialClient(LoanStatementEntity entity) {
        ClientEntity clientEntity = ClientEntity.builder()
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .birthdate(entity.getBirthdate())
                .email(entity.getEmail())
                .passportId(buildPassportId(entity))
                .build();
        clientEntity = clientRepository.save(clientEntity);
        log.info("Created client from loan statement request: {}", clientEntity);
        return clientEntity;
    }

    @Override
    @Transactional
    public void updateClientInformationFromScoringData(ScoringDataEntity scoringDataEntity) {
        ClientEntity clientEntity = clientRepository.findClientEntityByPassportId(
                scoringDataEntity.getPassportSeries(), scoringDataEntity.getPassportNumber())
                .orElseThrow(() -> new ClientNotFoundException("Client not found with passport: "
                        + scoringDataEntity.getPassportSeries() + " " + scoringDataEntity.getPassportNumber()));

        log.info("Updating client information for client ID: {}", clientEntity.getClientId());

        clientEntity.setBirthdate(scoringDataEntity.getBirthdate());
        clientEntity.setGender(scoringDataEntity.getGender());
        clientEntity.setMaritalStatus(scoringDataEntity.getMaritalStatus());
        clientEntity.setMiddleName(scoringDataEntity.getMiddleName());
        clientEntity.setDependentAmount(scoringDataEntity.getDependentAmount());
        clientEntity.getPassportId().setIssuedDate(scoringDataEntity.getPassportIssueDate());
        clientEntity.getPassportId().setIssueBranch(scoringDataEntity.getPassportIssueBranch());
        clientEntity.setEmploymentId(scoringDataEntity.getEmployment());

        clientRepository.save(clientEntity);
        log.info("Updated client information: {}", clientEntity);
    }

    private PassportId buildPassportId(LoanStatementEntity entity) {
        return PassportId.builder()
                .series(entity.getPassportSeries())
                .number(entity.getPassportNumber())
                .build();
    }
}

