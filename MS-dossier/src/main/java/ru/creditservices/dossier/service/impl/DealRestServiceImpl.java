package ru.creditservices.dossier.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.dossier.config.DealServiceProperties;
import ru.creditservices.dossier.service.DealRestService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealRestServiceImpl implements DealRestService {

    private final RestClient dealRestClient;
    private final DealServiceProperties dealServiceProperties;

    @Override
    public void updateStatementStatus(UUID statementId, String status) {
        log.info("Sending PUT request to update statement status: statementId={}, status={}", statementId, status);
        try {
            dealRestClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path(dealServiceProperties.getPutPath())
                            .queryParam("statementId", statementId)
                            .queryParam("status", status)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully updated statement status: statementId={}, status={}", statementId, status);

        } catch (RestClientResponseException ex) {
            HttpStatusCode sc = ex.getStatusCode();
            log.error("Deal service returned error {} {}: {}", sc.value(), ex.getStatusText(),
                    ex.getResponseBodyAsString());
            throw ex;

        } catch (RestClientException ex) {
            log.error("Error while sending request to Deal service: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
