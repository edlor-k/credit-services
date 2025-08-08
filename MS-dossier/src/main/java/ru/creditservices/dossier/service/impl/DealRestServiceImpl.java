package ru.creditservices.dossier.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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
            String url = dealServiceProperties.getBaseUrl() +
                    dealServiceProperties.getPutPath()
                            .replace("{statementId}", statementId.toString())
                            .replace("{status}", status);

            dealRestClient
                    .put()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Successfully updated statement status: statementId={}, status={}", statementId, status);
        } catch (HttpClientErrorException e) {
            log.error("Client error while updating statement status: {}", e.getMessage(), e);
            throw e;
        } catch (RestClientException e) {
            log.error("Error while sending request to Deal service: {}", e.getMessage(), e);
            throw e;
        }
    }

}
