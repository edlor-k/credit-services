package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.deal.config.CalculatorServiceProperties;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.ErrorResponseDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.ScoringDataDto;
import ru.creditservices.deal.exception.CalculatorServiceException;
import ru.creditservices.deal.model.enums.ErrorCode;
import ru.creditservices.deal.service.CalculatorClientService;
import ru.creditservices.deal.service.ErrorResponseParserService;

import java.util.List;
import java.util.Map;

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
            log.warn("Calculator /offers returned empty or service unavailable");
            throw unavailableError("/offers");
        }

        List<LoanOfferDto> offers = errorResponseParserService.parseLoanOffersResponse(response);

        if (offers == null || offers.isEmpty()) {
            log.warn("Calculator /offers returned empty list");
            throw new CalculatorServiceException(
                    ErrorResponseDto.builder()
                            .code(ErrorCode.CLIENT_ERROR)
                            .message("Калькулятор вернул пустой список предложений")
                            .details(Map.of("endpoint", "/offers"))
                            .build()
            );
        }

        return offers;
    }

    @Override
    public CreditDto fetchCalculatorResult(ScoringDataDto dto) {
        String response = executePost(calculatorServiceProperties.getCalcPath(), dto);
        if (response == null) {
            log.warn("Calculator /calc returned empty or service unavailable");
            throw unavailableError("/calc");
        }

        CreditDto credit = errorResponseParserService.parseCalculatorResultResponse(response);

        if (credit == null || credit.getAmount() == null) {
            log.warn("Calculator /calc returned empty or invalid CreditDto: {}", credit);
            throw new CalculatorServiceException(
                    ErrorResponseDto.builder()
                            .code(ErrorCode.CLIENT_ERROR)
                            .message("Калькулятор вернул пустой или некорректный результат")
                            .details(Map.of("endpoint", "/calc"))
                            .build()
            );
        }

        return credit;
    }

    private String executePost(String uri, Object dto) {
        try {
            String body = calculatorRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .body(String.class);

            return isBlank(body) ? null : body;

        } catch (RestClientResponseException ex) {
            String response = ex.getResponseBodyAsString();
            var sc = ex.getStatusCode();
            String statusStr = (sc != null ? sc.toString() : "unknown");
            String statusText = (ex.getStatusText() != null ? ex.getStatusText() : "");
            log.warn("Calculator {} HTTP error: status={} {}, bodyPresent={}",
                    uri, statusStr, statusText, !isBlank(response));
            return isBlank(response) ? null : response;
        }catch (RestClientException ex) {
            log.error("Calculator {} unavailable: {}", uri, ex.getMessage());
            return null;
        }
    }

    private CalculatorServiceException unavailableError(String endpoint) {
        return new CalculatorServiceException(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INTERNAL_ERROR)
                        .message("Калькулятор недоступен или вернул пустой ответ")
                        .details(Map.of("endpoint", endpoint))
                        .build()
        );
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
