package ru.creditservices.statement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.statement.config.DealServiceProperties;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.exception.DealServiceException;
import ru.creditservices.statement.model.enums.ErrorCode;
import ru.creditservices.statement.service.DealClientService;
import ru.creditservices.statement.service.DealErrorHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealClientServiceImpl implements DealClientService {

    private final RestClient dealRestClient;
    private final DealServiceProperties dealServiceProperties;
    private final DealErrorHandler errorHandler;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto) {
        try {
            LoanOfferDto[] body = dealRestClient.post()
                    .uri(dealServiceProperties.getStatementPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .body(LoanOfferDto[].class);

            if (body == null || body.length == 0) {
                log.warn("Deal /statement returned empty list");
                throw new DealServiceException(
                        errorHandler.buildError(
                                ErrorCode.CLIENT_ERROR,
                                "Deal вернул пустой список предложений",
                                dealServiceProperties.getStatementPath()
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            return Arrays.asList(Objects.requireNonNull(body));

        } catch (RestClientResponseException ex) {
            errorHandler.logHttpError(ex, dealServiceProperties.getStatementPath());
            throw new DealServiceException(
                    errorHandler.parseErrorOrFallback(
                            ex.getResponseBodyAsString(),
                            ErrorCode.INTERNAL_ERROR,
                            "Ошибка ответа Deal",
                            dealServiceProperties.getStatementPath()
                    ),
                    HttpStatus.valueOf(ex.getStatusCode().value())
            );

        } catch (RestClientException ex) {
            log.error("Get offers request unavailable at {}: {}",
                    dealServiceProperties.getStatementPath(), ex.getMessage());
            throw new DealServiceException(
                    errorHandler.buildError(
                            ErrorCode.INTERNAL_ERROR,
                            "MS Deal недоступен или вернул пустой ответ",
                            dealServiceProperties.getStatementPath()
                    ),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        try {
            dealRestClient.post()
                    .uri(dealServiceProperties.getOfferPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(loanOfferDto)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {
            errorHandler.logHttpError(ex, dealServiceProperties.getOfferPath());
            throw new DealServiceException(
                    errorHandler.parseErrorOrFallback(
                            ex.getResponseBodyAsString(),
                            ErrorCode.INTERNAL_ERROR,
                            "Ошибка ответа Deal",
                            dealServiceProperties.getOfferPath()
                    ),
                    HttpStatus.valueOf(ex.getStatusCode().value())
            );

        } catch (RestClientException ex) {
            log.error("Select offer request unavailable at {}: {}",
                    dealServiceProperties.getOfferPath(), ex.getMessage());
            throw new DealServiceException(
                    errorHandler.buildError(
                            ErrorCode.INTERNAL_ERROR,
                            "MS Deal недоступен или вернул пустой ответ",
                            dealServiceProperties.getOfferPath()
                    ),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
}
