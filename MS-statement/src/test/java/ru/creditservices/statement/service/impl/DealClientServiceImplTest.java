package ru.creditservices.statement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.statement.config.DealServiceProperties;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.exception.DealServiceException;
import ru.creditservices.statement.service.DealErrorHandler;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealClientServiceImplTest {

    @Mock
    private DealServiceProperties props;

    @Mock
    private DealErrorHandler errorHandler;

    @Mock
    private org.springframework.web.client.RestClient restClient;

    @InjectMocks
    private DealClientServiceImpl service;

    @Test
    void fetchLoanOffers_throwsDealServiceException_whenRestClientResponseException() {
        when(props.getStatementPath()).thenReturn("/statement");

        RestClientResponseException ex = new RestClientResponseException(
                "Bad request",
                HttpStatus.BAD_REQUEST.value(),
                "Bad request",
                null,
                "err".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        when(restClient.post()).thenThrow(ex);

        assertThatThrownBy(() -> service.fetchLoanOffers(new LoanStatementRequestDto()))
                .isInstanceOf(DealServiceException.class);
    }

    @Test
    void fetchLoanOffers_throwsDealServiceException_whenRestClientException() {
        when(props.getStatementPath()).thenReturn("/statement");

        when(restClient.post()).thenThrow(new RestClientException("Service unavailable"));

        assertThatThrownBy(() -> service.fetchLoanOffers(new LoanStatementRequestDto()))
                .isInstanceOf(DealServiceException.class);
    }

    @Test
    void selectLoanOffer_throwsDealServiceException_whenRestClientResponseException() {
        when(props.getOfferPath()).thenReturn("/offer");

        RestClientResponseException ex = new RestClientResponseException(
                "Bad request",
                HttpStatus.BAD_REQUEST.value(),
                "Bad request",
                null,
                "err".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        when(restClient.post()).thenThrow(ex);

        assertThatThrownBy(() -> service.selectLoanOffer(new LoanOfferDto()))
                .isInstanceOf(DealServiceException.class);
    }

    @Test
    void selectLoanOffer_throwsDealServiceException_whenRestClientException() {
        when(props.getOfferPath()).thenReturn("/offer");

        when(restClient.post()).thenThrow(new RestClientException("Connection lost"));

        assertThatThrownBy(() -> service.selectLoanOffer(new LoanOfferDto()))
                .isInstanceOf(DealServiceException.class);
    }
}
