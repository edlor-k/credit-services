package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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
        String response = executePost("/calculator/offers", dto);
        if (response == null) {
            log.warn("Calculator /offers returned empty response");
            return List.of();
        }
        return errorResponseParserService.parseLoanOffersResponse(response);
    }

    @Override
    public CalculatorResult fetchCalculatorResult(ScoringDataDto dto) {
        String response = executePost("/calculator/calc", dto);
        if (response == null) {
            log.warn("Calculator /calc returned empty response");
            return CalculatorResult.requestError("Пустой ответ от калькулятора", List.of());
        }
        return errorResponseParserService.parseCalculatorResultResponse(response);
    }

    private String executePost(String uri, Object dto) {
        try {
            return calculatorRestClient.post()
                    .uri(uri)
                    .body(dto)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException ex) {
            String response = ex.getResponseBodyAsString();
            log.warn("Calculator {} returned HTTP error: {}, body: {}", uri, ex.getStatusCode(), response);
            return response;
        } catch (RestClientException ex) {
            log.error("Calculator {} unavailable or unknown error: {}", uri, ex.getMessage(), ex);
            return null;
        }
    }
}
