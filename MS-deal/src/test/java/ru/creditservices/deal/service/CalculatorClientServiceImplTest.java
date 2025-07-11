package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.creditservices.deal.config.CalculatorServiceProperties;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.service.impl.CalculatorClientServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculatorClientServiceImplTest {

    @Mock
    private RestClient calculatorRestClient;
    @Mock
    private ErrorResponseParserService errorResponseParserService;
    @Mock
    private CalculatorServiceProperties calculatorServiceProperties;

    @InjectMocks
    private CalculatorClientServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(calculatorServiceProperties.getOffersPath()).thenReturn("/offers");
        when(calculatorServiceProperties.getCalcPath()).thenReturn("/calc");
    }

    @Test
    @DisplayName("Обработка HTTP ошибок при получении кредитных предложений (1 вид)")
    void fetchLoanOffersParsesErrorBodyIfHttpError() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        String errorJson = "{\"error\":\"err\"}";
        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getResponseBodyAsString()).thenReturn(errorJson);
        when(calculatorRestClient.post()).thenThrow(ex);

        List<LoanOfferDto> offers = List.of(new LoanOfferDto());
        when(errorResponseParserService.parseLoanOffersResponse(errorJson)).thenReturn(offers);

        List<LoanOfferDto> result = service.fetchLoanOffers(requestDto);
        assertThat(result).isSameAs(offers);
        verify(errorResponseParserService).parseLoanOffersResponse(errorJson);
    }

    @Test
    @DisplayName("Обработка HTTP ошибок при получении кредитных предложений (2 вид)")
    void fetchCalculatorResultParsesErrorBodyIfHttpError() {
        ScoringDataDto dto = new ScoringDataDto();
        String errorJson = "{\"violations\":[{\"fieldName\":\"someField\"}]}";
        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getResponseBodyAsString()).thenReturn(errorJson);
        when(calculatorRestClient.post()).thenThrow(ex);

        CalculatorResult fakeResult = CalculatorResult.requestError("err", List.of());
        when(errorResponseParserService.parseCalculatorResultResponse(errorJson)).thenReturn(fakeResult);

        CalculatorResult result = service.fetchCalculatorResult(dto);

        assertThat(result).isSameAs(fakeResult);
    }
}