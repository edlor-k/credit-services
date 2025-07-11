package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.model.entity.LoanOfferEntity;
import ru.creditservices.deal.service.SelectLoanOfferService;
import ru.creditservices.deal.service.StatementManagerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SelectLoanOfferServiceImpl implements SelectLoanOfferService {

    private final LoanOfferMapper loanOfferMapper;
    private final StatementManagerService statementManagerService;

    @Override
    @Transactional
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        LoanOfferEntity loanOfferEntity = loanOfferMapper.toEntity(loanOfferDto);
        statementManagerService.selectLoanOfferToStatement(loanOfferEntity);
        log.info("Loan offer selected for statementId={}", loanOfferEntity.getStatementId());
    }
}
