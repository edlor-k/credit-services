package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.service.CalculatorClientService;
import ru.creditservices.deal.service.ErrorResponseParserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculatorClientServiceImpl implements CalculatorClientService {

    private final RestClient calculatorRestClient;
    private final ErrorResponseParserService errorResponseParserService;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto) {
        String response;
        try {
            response = calculatorRestClient.post()
                    .uri("/calculator/offers")
                    .body(dto)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException ex) {
            response = ex.getResponseBodyAsString();
            log.warn("Calculator /offers returned error response: {}", response);
        }
        return errorResponseParserService.parseLoanOffersResponse(response);
    }

    @Override
    public CalculatorResult fetchCalculatorResult(ScoringDataDto dto) {
        String response;
        try {
            response = calculatorRestClient.post()
                    .uri("/calculator/calc")
                    .body(dto)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException ex) {
            response = ex.getResponseBodyAsString();
            log.warn("Calculator /calc returned error response: {}", response);
        }
        return errorResponseParserService.parseCalculatorResultResponse(response);
    }
}
