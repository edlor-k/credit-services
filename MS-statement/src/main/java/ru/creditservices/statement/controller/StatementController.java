package ru.creditservices.statement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.service.GetLoanOffersService;
import ru.creditservices.statement.service.SelectLoanOfferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatementController implements StatementApi{

    private final GetLoanOffersService getLoanOffersService;
    private final SelectLoanOfferService selectLoanOfferService;

    @Override
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(LoanStatementRequestDto request) {
        log.info("Request for getting loan offers: {}", request);
        List<LoanOfferDto> loanOffers = getLoanOffersService.getLoanOffers(request);
        log.debug("Loan offers: {}", loanOffers);
        return ResponseEntity.ok(loanOffers);
    }

    @Override
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOfferDto) {
        log.info("Request for selecting loan offer: {}", loanOfferDto);
        selectLoanOfferService.selectLoanOffer(loanOfferDto);
        log.debug("Loan offer selected");
        return ResponseEntity.ok().build();
    }
}
