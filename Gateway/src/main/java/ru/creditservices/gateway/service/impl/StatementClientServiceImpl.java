package ru.creditservices.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.creditservices.gateway.config.StatementServiceProperties;
import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;
import ru.creditservices.gateway.exception.StatementClientException;
import ru.creditservices.gateway.model.enums.RestClientErrorType;
import ru.creditservices.gateway.service.StatementClientService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementClientServiceImpl extends BaseRestClient implements StatementClientService {

    private final RestClient statementRestClient;
    private final StatementServiceProperties statementServiceProperties;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto request) {
        log.info("Вызов StatementClientService: fetchLoanOffers()");
        log.debug("Тело запроса: {}", request);
        return execute(() -> Arrays.asList(Objects.requireNonNull(
                        statementRestClient.post()
                                .uri(statementServiceProperties.getStatementPath())
                                .body(request)
                                .retrieve()
                                .body(LoanOfferDto[].class)
                )), "fetch loan offers",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        log.info("Вызов StatementClientService: selectLoanOffer()");
        log.debug("Запроса: {}", loanOfferDto);
        execute(() -> {
                    statementRestClient.post()
                            .uri(statementServiceProperties.getOfferPath())
                            .body(loanOfferDto)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                }, "select loan offer",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }
}
