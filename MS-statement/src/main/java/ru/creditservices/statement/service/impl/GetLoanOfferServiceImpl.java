package ru.creditservices.statement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.mapper.LoanStatementMapper;
import ru.creditservices.statement.model.entity.LoanStatementEntity;
import ru.creditservices.statement.service.DealClientService;
import ru.creditservices.statement.service.GetLoanOffersService;
import ru.creditservices.statement.service.PrescoringService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetLoanOfferServiceImpl implements GetLoanOffersService {

    private final PrescoringService prescoringService;
    private final LoanStatementMapper loanStatementMapper;
    private final DealClientService dealClientService;

    @Override
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
        log.info("Request for getting loan offers: {}", request);
        LoanStatementEntity loanStatementEntity = loanStatementMapper.toEntity(request);
        prescoringService.businessValidate(loanStatementEntity);
        log.debug("Loan statement validated: {}", loanStatementEntity);
        return dealClientService.fetchLoanOffers(request);
    }
}
