package ru.creditservices.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.creditservices.gateway.config.DealServiceProperties;
import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.StatementDto;
import ru.creditservices.gateway.exception.StatementClientException;
import ru.creditservices.gateway.model.enums.RestClientErrorType;
import ru.creditservices.gateway.service.DealClientService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealClientServiceImpl extends BaseRestClient implements DealClientService {

    private final RestClient dealRestClient;
    private final DealServiceProperties dealServiceProperties;

    @Override
    public void finishRegistration(FinishRegistrationRequestDto request, UUID statementId) {
        log.info("Вызов DealClientService: finishRegistration (statementId = {})", statementId);
        log.debug("Тело запроса: {}", request);
        execute(() -> {
                    dealRestClient.post()
                            .uri(dealServiceProperties.getCalculatePath(), statementId)
                            .body(request)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                }, "finish registration",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }

    @Override
    public void createDocuments(UUID statementId) {
        log.info("Вызов DealClientService: createDocuments (statementId = {})", statementId);
        execute(() -> {
                    dealRestClient.post()
                            .uri(dealServiceProperties.getCreateDocumentsPath(), statementId)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                }, "create documents",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }

    @Override
    public void signDocuments(UUID statementId) {
        log.info("Вызов DealClientService: signDocuments (statementId = {})", statementId);
        execute(() -> {
                    dealRestClient.post()
                            .uri(dealServiceProperties.getSignDocumentsPath(), statementId)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                }, "sign documents",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }

    @Override
    public void verifyDocuments(UUID statementId, String sesCode) {
        log.info("Вызов DealClientService: verifyDocuments (statementId = {}, sesCode = {})", statementId, sesCode);
        execute(() -> {
                    dealRestClient.post()
                            .uri(dealServiceProperties.getVerifyDocumentsPath(), statementId, sesCode)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                }, "verify documents",
                msg -> new StatementClientException(RestClientErrorType.REQUEST_ERROR, msg));
    }

    @Override
    public List<StatementDto> fetchStatements() {
        log.info("Вызов DealClientService: fetchStatements()");
        return execute(() -> Arrays.asList(
                        Objects.requireNonNull(
                                dealRestClient.get()
                                        .uri(dealServiceProperties.getAdminGetAllStatementsPath())
                                        .retrieve()
                                        .body(StatementDto[].class)
                        )
                ), "fetch all statements",
                msg -> new StatementClientException(RestClientErrorType.EMPTY_RESPONSE, msg));
    }

    @Override
    public StatementDto fetchStatementById(UUID statementId) {
        log.info("Вызов DealClientService: fetchStatementById (statementId = {})", statementId);
        return execute(() -> Objects.requireNonNull(
                        dealRestClient.get()
                                .uri(dealServiceProperties.getAdminGetStatementPath(), statementId)
                                .retrieve()
                                .body(StatementDto.class)
                ), "fetch statement by ID",
                msg -> new StatementClientException(RestClientErrorType.EMPTY_RESPONSE, msg));
    }

}
