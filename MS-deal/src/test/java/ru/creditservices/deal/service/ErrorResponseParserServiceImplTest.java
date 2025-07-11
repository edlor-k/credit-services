package ru.creditservices.deal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.exception.CalculatorValidationException;
import ru.creditservices.deal.model.enums.CalculatorErrorType;
import ru.creditservices.deal.service.impl.ErrorResponseParserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseParserServiceImplTest {

    private ErrorResponseParserServiceImpl parser;

    @BeforeEach
    void setUp() {
        parser = new ErrorResponseParserServiceImpl(new ObjectMapper());
    }

    @Test
    void parseLoanOffersResponse_shouldParseList() {
        String json = """
                [
                    {"requestedAmount":500000,"totalAmount":550000,"term":24,"monthlyPayment":22916.67,"rate":14.9,"isInsuranceEnabled":true,"isSalaryClient":true}
                ]
                """;
        List<LoanOfferDto> offers = parser.parseLoanOffersResponse(json);
        assertEquals(1, offers.size());
        assertEquals(500000, offers.getFirst().getRequestedAmount().intValue());
    }

    @Test
    void parseLoanOffersResponse_shouldThrowForViolation() {
        String json = """
                {"violations":[{"fieldName":"firstName","message":"Обязательное поле"}]}
                """;
        CalculatorValidationException ex = assertThrows(
                CalculatorValidationException.class,
                () -> parser.parseLoanOffersResponse(json)
        );
        assertEquals(CalculatorErrorType.REQUEST_ERROR, ex.getErrorType());
        assertFalse(ex.getViolations().isEmpty());
    }

    @Test
    void parseLoanOffersResponse_shouldThrowParseError() {
        String invalidJson = "not a json";
        assertThrows(
                RuntimeException.class,
                () -> parser.parseLoanOffersResponse(invalidJson)
        );
    }

    @Test
    void parseCalculatorResultResponse_shouldReturnRequestError() {
        String json = """
            {"violations":[{"fieldName":"request","message":"Ошибка"}]}
            """;
        var result = parser.parseCalculatorResultResponse(json);
        assertEquals("REQUEST_ERROR", result.type().name());
        assertEquals("Ошибка", result.requestErrorMessage());
    }
}