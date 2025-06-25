package ru.creditservices.deal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
        log.info("Got request for getting loan offers: {}", loanStatementRequestDto);
        LoanStatementEntity loanStatementEntity = loanStatementMapper.toEntity(loanStatementRequestDto);

        List<LoanOfferDto> loanOfferDtoList = calculatorClientService.fetchLoanOffers(loanStatementRequestDto);

        log.info("Fetched {} loan offers: {}", loanOfferDtoList.size(), loanOfferDtoList);
        List<LoanOfferEntity> loanOfferList = loanOfferDtoList.stream()
                .map(loanOfferMapper::toEntity)
                .toList();

        ClientEntity clientEntity = createClientService.createClientFromLoanStatementRequest(loanStatementEntity);
        log.info("Got/Created client with id {}", clientEntity.getClientId());

        StatementEntity statementEntity = statementManagerService.createStatementFromClient(clientEntity);
        log.info("Got/Created statement with id {}", statementEntity.getStatementId());

        loanOfferList.forEach(offer -> offer.setStatementId(statementEntity.getStatementId()));
        loanOfferDtoList = loanOfferList.stream()
                .map(loanOfferMapper::toDto)
                .toList();
        return loanOfferDtoList;
    }
}
