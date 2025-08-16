package ru.creditservices.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.exception.CalculatorServiceException;
import ru.creditservices.deal.exception.ParseCalculatorException;
import ru.creditservices.deal.model.enums.ErrorCode;
import ru.creditservices.deal.service.impl.ErrorResponseParserServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseParserServiceImplTest {

    private ErrorResponseParserServiceImpl parser;

    @BeforeEach
    void setUp() {
        parser = new ErrorResponseParserServiceImpl(new ObjectMapper());
    }

    @Test
    @DisplayName("parseLoanOffersResponse: корректный список офферов парсится успешно")
    void parseLoanOffersResponse_shouldParseList() {
        String json = """
                [
                  {
                    "requestedAmount":500000,
                    "totalAmount":550000,
                    "term":24,
                    "monthlyPayment":22916.67,
                    "rate":14.9,
                    "isInsuranceEnabled":true,
                    "isSalaryClient":true
                  }
                ]
                """;

        List<LoanOfferDto> offers = parser.parseLoanOffersResponse(json);

        assertNotNull(offers);
        assertEquals(1, offers.size());
        assertEquals(500000, offers.getFirst().getRequestedAmount().intValue());
    }

    @Test
    @DisplayName("parseLoanOffersResponse: если пришёл ErrorResponseDto, кидается CalculatorServiceException")
    void parseLoanOffersResponse_shouldThrowCalculatorServiceExceptionForErrorDto() {
        String json = """
                {
                  "code":"INVALID_ARGUMENT",
                  "message":"Ошибка валидации",
                  "details":{"field":"term"}
                }
                """;

        CalculatorServiceException ex = assertThrows(
                CalculatorServiceException.class,
                () -> parser.parseLoanOffersResponse(json)
        );

        assertNotNull(ex.getError());
        assertEquals(ErrorCode.INVALID_ARGUMENT, ex.getError().getCode());
        assertEquals("Ошибка валидации", ex.getError().getMessage());
        assertTrue(ex.getError().getDetails().containsKey("field"));
    }

    @Test
    @DisplayName("parseLoanOffersResponse: непонятное тело → ParseCalculatorException")
    void parseLoanOffersResponse_shouldThrowParseCalculatorExceptionWhenUnknownPayload() {
        String invalidJson = "not a json";
        assertThrows(ParseCalculatorException.class, () -> parser.parseLoanOffersResponse(invalidJson));
    }

    @Test
    @DisplayName("parseCalculatorResultResponse: корректный CreditDto парсится успешно")
    void parseCalculatorResultResponse_shouldParseCreditDto() {
        String json = """
                {
                  "amount": 550000,
                  "term": 24,
                  "rate": 14.9
                }
                """;

        CreditDto credit = parser.parseCalculatorResultResponse(json);

        assertNotNull(credit);
        assertEquals(new BigDecimal("550000"), credit.getAmount());
    }

    @Test
    @DisplayName("parseCalculatorResultResponse: если пришёл ErrorResponseDto, кидается CalculatorServiceException")
    void parseCalculatorResultResponse_shouldThrowCalculatorServiceExceptionForErrorDto() {
        String json = """
                {
                  "code":"CLIENT_ERROR",
                  "message":"Отказ по бизнес-правилам",
                  "details":{"business":"reason"}
                }
                """;

        CalculatorServiceException ex = assertThrows(
                CalculatorServiceException.class,
                () -> parser.parseCalculatorResultResponse(json)
        );

        assertNotNull(ex.getError());
        assertEquals(ErrorCode.CLIENT_ERROR, ex.getError().getCode());
        assertEquals("Отказ по бизнес-правилам", ex.getError().getMessage());
        assertTrue(ex.getError().getDetails().containsKey("business"));
    }

    @Test
    @DisplayName("parseCalculatorResultResponse: мусор → ParseCalculatorException")
    void parseCalculatorResultResponse_shouldThrowParseCalculatorExceptionForGarbage() {
        String garbage = "{\"this\":\"is not credit nor error\"";
        assertThrows(ParseCalculatorException.class, () -> parser.parseCalculatorResultResponse(garbage));
    }
}
