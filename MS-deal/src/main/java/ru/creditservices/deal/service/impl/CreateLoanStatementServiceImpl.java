package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.mapper.LoanStatementMapper;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.LoanOfferEntity;
import ru.creditservices.deal.model.entity.LoanStatementEntity;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.service.CalculatorClientService;
import ru.creditservices.deal.service.ClientManagerService;
import ru.creditservices.deal.service.CreateLoanStatementService;
import ru.creditservices.deal.service.StatementManagerService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateLoanStatementServiceImpl implements CreateLoanStatementService {

    private final LoanStatementMapper loanStatementMapper;
    private final LoanOfferMapper loanOfferMapper;

    private final ClientManagerService createClientService;
    private final StatementManagerService statementManagerService;
    private final CalculatorClientService calculatorClientService;

    @Override
    @Transactional
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("Processing loan statement request");

        LoanStatementEntity loanStatementEntity = loanStatementMapper.toEntity(loanStatementRequestDto);

        List<LoanOfferDto> offersFromCalculator = calculatorClientService.fetchLoanOffers(loanStatementRequestDto);

        ClientEntity client = createClientService.createClient(loanStatementEntity);
        log.info("Got/Created client with id {}", client.getClientId());

        StatementEntity statement = statementManagerService.createStatementFromClient(client);
        log.info("Got/Created statement with id {}", statement.getStatementId());

        List<LoanOfferEntity> entities = offersFromCalculator.stream()
                .map(loanOfferMapper::toEntity)
                .peek(e -> e.setStatementId(statement.getStatementId()))
                .toList();

        return entities.stream()
                .map(loanOfferMapper::toDto)
                .toList();
    }
}
