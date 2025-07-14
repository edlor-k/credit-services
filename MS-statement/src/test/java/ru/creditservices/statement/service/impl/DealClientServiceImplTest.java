package ru.creditservices.statement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.creditservices.statement.config.DealServiceProperties;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.exception.DealClientException;
import ru.creditservices.statement.model.enums.DealClientErrorType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealClientServiceImplTest {

    @Mock
    private RestClient dealRestClient;
    @Mock
    private DealServiceProperties dealServiceProperties;

    @Mock
    private RestClient.RequestBodyUriSpec postSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private DealClientServiceImpl dealClientService;

    @Test
    void fetchLoanOffersReturnsLoanOffersWhenRequestIsSuccessful() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanOfferDto[] loanOffers = {new LoanOfferDto(), new LoanOfferDto()};

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getStatementPath()).thenReturn("/statement");
        when(postSpec.uri("/statement")).thenReturn(bodySpec);
        when(bodySpec.body(requestDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(LoanOfferDto[].class)).thenReturn(loanOffers);

        List<LoanOfferDto> result = dealClientService.fetchLoanOffers(requestDto);

        assertEquals(2, result.size());
    }

    @Test
    void fetchLoanOffersThrowsDealClientExceptionWhenHttpClientErrorOccurs() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getStatementPath()).thenReturn("/statement");
        when(postSpec.uri("/statement")).thenReturn(bodySpec);
        when(bodySpec.body(requestDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST, "Bad Request"));

        DealClientException exception = assertThrows(DealClientException.class, () ->
                dealClientService.fetchLoanOffers(requestDto));

        assertEquals(DealClientErrorType.REQUEST_ERROR, exception.getErrorType());
    }

    @Test
    void fetchLoanOffersThrowsDealClientExceptionWhenRestClientExceptionOccurs() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getStatementPath()).thenReturn("/statement");
        when(postSpec.uri("/statement")).thenReturn(bodySpec);
        when(bodySpec.body(requestDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(new RestClientException("Service Unavailable"));

        DealClientException exception = assertThrows(DealClientException.class, () ->
                dealClientService.fetchLoanOffers(requestDto));

        assertEquals(DealClientErrorType.EMPTY_RESPONSE, exception.getErrorType());
    }

    @Test
    void selectLoanOfferCompletesSuccessfullyWhenRequestIsSuccessful() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getOfferPath()).thenReturn("/offer");
        when(postSpec.uri("/offer")).thenReturn(bodySpec);
        when(bodySpec.body(loanOfferDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> dealClientService.selectLoanOffer(loanOfferDto));
    }

    @Test
    void selectLoanOfferThrowsDealClientExceptionWhenHttpClientErrorOccurs() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getOfferPath()).thenReturn("/offer");
        when(postSpec.uri("/offer")).thenReturn(bodySpec);
        when(bodySpec.body(loanOfferDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST, "Bad Request"));

        DealClientException exception = assertThrows(DealClientException.class, () ->
                dealClientService.selectLoanOffer(loanOfferDto));

        assertEquals(DealClientErrorType.REQUEST_ERROR, exception.getErrorType());
    }

    @Test
    void selectLoanOfferThrowsDealClientExceptionWhenRestClientExceptionOccurs() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        when(dealRestClient.post()).thenReturn(postSpec);
        when(dealServiceProperties.getOfferPath()).thenReturn("/offer");
        when(postSpec.uri("/offer")).thenReturn(bodySpec);
        when(bodySpec.body(loanOfferDto)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(new RestClientException("Service Unavailable"));

        DealClientException exception = assertThrows(DealClientException.class, () ->
                dealClientService.selectLoanOffer(loanOfferDto));

        assertEquals(DealClientErrorType.EMPTY_RESPONSE, exception.getErrorType());
    }
}
