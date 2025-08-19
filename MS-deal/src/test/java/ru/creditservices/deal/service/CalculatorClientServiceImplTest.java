package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.creditservices.deal.config.CalculatorServiceProperties;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.ScoringDataDto;
import ru.creditservices.deal.exception.CalculatorServiceException;
import ru.creditservices.deal.service.impl.CalculatorClientServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculatorClientServiceImplTest {

    @Mock private RestClient calculatorRestClient;
    @Mock private ErrorResponseParserService errorResponseParserService;
    @Mock private CalculatorServiceProperties calculatorServiceProperties;

    @InjectMocks
    private CalculatorClientServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(calculatorServiceProperties.getOffersPath()).thenReturn("/offers");
        when(calculatorServiceProperties.getCalcPath()).thenReturn("/calc");
    }

    @Test
    @DisplayName("fetchLoanOffers: при HTTP-ошибке тело ошибки передаётся в парсер")
    void fetchLoanOffers_parsesErrorBodyIfHttpError() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        String errorJson = "{\"code\":\"INVALID_ARGUMENT\",\"message\":\"err\"}";

        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getResponseBodyAsString()).thenReturn(errorJson);
        when(ex.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST); // +
        when(ex.getStatusText()).thenReturn("Bad Request"); // +
        when(calculatorRestClient.post()).thenThrow(ex);

        List<LoanOfferDto> offers = List.of(new LoanOfferDto());
        when(errorResponseParserService.parseLoanOffersResponse(errorJson)).thenReturn(offers);

        List<LoanOfferDto> result = service.fetchLoanOffers(requestDto);

        assertThat(result).isSameAs(offers);
        verify(errorResponseParserService).parseLoanOffersResponse(errorJson);
    }

    @Test
    @DisplayName("fetchLoanOffers: недоступность/пустой ответ → CalculatorServiceException(INTERNAL_ERROR)")
    void fetchLoanOffers_throwsIfServiceUnavailableOrEmpty() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        when(calculatorRestClient.post()).thenThrow(new RestClientException("down"));

        assertThatThrownBy(() -> service.fetchLoanOffers(dto))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("недоступен");
        verifyNoInteractions(errorResponseParserService);
    }

    @Test
    @DisplayName("fetchLoanOffers: 2xx тело → парсится; пустой список → CalculatorServiceException(CLIENT_ERROR)")
    void fetchLoanOffers_emptyListFromParserThrows() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        String okJson = "[{\"dummy\":\"value\"}]";
        stubPostChainReturnBody("/offers", okJson, dto);

        when(errorResponseParserService.parseLoanOffersResponse(okJson)).thenReturn(List.of());

        assertThatThrownBy(() -> service.fetchLoanOffers(dto))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("пустой список");
    }

    @Test
    @DisplayName("fetchLoanOffers: успех → возвращает список офферов")
    void fetchLoanOffers_success() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        String okJson = "[{\"rate\":10}]";
        stubPostChainReturnBody("/offers", okJson, dto);

        List<LoanOfferDto> offers = List.of(new LoanOfferDto());
        when(errorResponseParserService.parseLoanOffersResponse(okJson)).thenReturn(offers);

        List<LoanOfferDto> result = service.fetchLoanOffers(dto);

        assertThat(result).isSameAs(offers);
    }

    @Test
    @DisplayName("fetchCalculatorResult: при HTTP-ошибке тело ошибки передаётся в парсер")
    void fetchCalculatorResult_parsesErrorBodyIfHttpError() {
        ScoringDataDto dto = new ScoringDataDto();
        String errorJson = "{\"code\":\"CLIENT_ERROR\",\"message\":\"decline\"}";

        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getResponseBodyAsString()).thenReturn(errorJson);
        when(ex.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST);
        when(ex.getStatusText()).thenReturn("Bad Request");
        when(calculatorRestClient.post()).thenThrow(ex);

        CreditDto credit = mock(CreditDto.class);
        when(credit.getAmount()).thenReturn(BigDecimal.ONE);
        when(errorResponseParserService.parseCalculatorResultResponse(errorJson)).thenReturn(credit);

        CreditDto result = service.fetchCalculatorResult(dto);

        assertThat(result).isSameAs(credit);
        verify(errorResponseParserService).parseCalculatorResultResponse(errorJson);
    }

    @Test
    @DisplayName("fetchCalculatorResult: 2xx тело → парсится; null/amount=null → " +
            "CalculatorServiceException(CLIENT_ERROR)")
    void fetchCalculatorResult_invalidCreditThrows() {
        ScoringDataDto dto = new ScoringDataDto();
        String okJson = "{\"credit\":\"payload\"}";
        stubPostChainReturnBody("/calc", okJson, dto);

        CreditDto invalid = mock(CreditDto.class);
        when(invalid.getAmount()).thenReturn(null);
        when(errorResponseParserService.parseCalculatorResultResponse(okJson)).thenReturn(invalid);

        assertThatThrownBy(() -> service.fetchCalculatorResult(dto))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("пустой или некорректный");
    }

    @Test
    @DisplayName("fetchCalculatorResult: успех → возвращает CreditDto")
    void fetchCalculatorResult_success() {
        ScoringDataDto dto = new ScoringDataDto();
        String okJson = "{\"credit\":\"payload\"}";
        stubPostChainReturnBody("/calc", okJson, dto);

        CreditDto credit = mock(CreditDto.class);
        when(credit.getAmount()).thenReturn(BigDecimal.TEN);
        when(errorResponseParserService.parseCalculatorResultResponse(okJson)).thenReturn(credit);

        CreditDto result = service.fetchCalculatorResult(dto);

        assertThat(result).isSameAs(credit);
    }

    private void stubPostChainReturnBody(String path, String responseBody, Object requestBody) {
        RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class, Answers.RETURNS_DEEP_STUBS);
        when(calculatorRestClient.post()).thenReturn(postSpec);
        when(postSpec
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class))
                .thenReturn(responseBody);
    }
}
