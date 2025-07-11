package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.creditservices.deal.config.CalculatorServiceProperties;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.exception.CalculatorValidationException;
import ru.creditservices.deal.model.enums.CalculatorErrorType;
import ru.creditservices.deal.service.CalculatorClientService;
import ru.creditservices.deal.service.ErrorResponseParserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculatorClientServiceImpl implements CalculatorClientService {

    private final RestClient calculatorRestClient;
    private final ErrorResponseParserService errorResponseParserService;
    private final CalculatorServiceProperties calculatorServiceProperties;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto) {
        String response = executePost(calculatorServiceProperties.getOffersPath(), dto);
        if (response == null) {
            log.warn("Calculator /offers returned empty response");
            throw new CalculatorValidationException(
                    CalculatorErrorType.REQUEST_ERROR,
                    List.of(new Violation("request",
                            "Ошибка: Калькулятор недоступен или вернул пустой ответ"))
            );
        }
        return errorResponseParserService.parseLoanOffersResponse(response);
    }

    @Override
    public CalculatorResult fetchCalculatorResult(ScoringDataDto dto) {
        String response = executePost(calculatorServiceProperties.getCalcPath(), dto);
        if (response == null) {
            log.warn("Calculator /calc returned empty response");
            throw new CalculatorValidationException(
                    CalculatorErrorType.REQUEST_ERROR,
                    List.of(new Violation("request",
                            "Ошибка: Калькулятор недоступен или вернул пустой ответ"))
            );
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
            log.warn("Calculator {} HTTP error: {}", uri, ex.getStatusCode());
            return response;
        } catch (RestClientException ex) {
            log.error("Calculator {} unavailable: {}", uri, ex.getMessage());
            return null;
        }
    }
}
