package ru.creditservices.statement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.service.DealClientService;
import ru.creditservices.statement.service.SelectLoanOfferService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectLoanOfferServiceImpl implements SelectLoanOfferService {

    private final DealClientService dealClientService;

    @Override
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        log.info("Selecting loan offer: {}", loanOfferDto);
        dealClientService.selectLoanOffer(loanOfferDto);
        log.debug("Loan offer selected successfully: {}", loanOfferDto);
    }
}
