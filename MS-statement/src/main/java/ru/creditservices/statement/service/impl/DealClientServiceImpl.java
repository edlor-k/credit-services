package ru.creditservices.statement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.creditservices.statement.config.DealServiceProperties;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.exception.DealClientException;
import ru.creditservices.statement.model.enums.DealClientErrorType;
import ru.creditservices.statement.service.DealClientService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealClientServiceImpl implements DealClientService {

    private final RestClient dealRestClient;
    private final DealServiceProperties dealServiceProperties;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto) {
        try {
            return Arrays.asList(
                    Objects.requireNonNull(dealRestClient.post()
                            .uri(dealServiceProperties.getStatementPath())
                            .body(dto)
                            .retrieve()
                            .body(LoanOfferDto[].class))
            );
        } catch (HttpClientErrorException ex) {
            log.warn("Deal MS HTTP error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new DealClientException(DealClientErrorType.REQUEST_ERROR, ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            log.error("Deal MS unavailable: {}", ex.getMessage());
            throw new DealClientException(DealClientErrorType.EMPTY_RESPONSE, ex.getMessage());
        }
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        try {
            dealRestClient.post()
                    .uri(dealServiceProperties.getOfferPath())
                    .body(loanOfferDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.warn("Deal MS HTTP error on selectLoanOffer ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new DealClientException(DealClientErrorType.REQUEST_ERROR, ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            log.error("Deal MS unavailable on selectLoanOffer: {}", ex.getMessage());
            throw new DealClientException(DealClientErrorType.EMPTY_RESPONSE,
                    "MS Deal недоступен или вернул пустой ответ");
        }
    }
}