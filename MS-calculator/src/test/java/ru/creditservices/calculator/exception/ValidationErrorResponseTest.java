package ru.creditservices.calculator.exception;

import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.dto.ValidationErrorResponse;
import ru.creditservices.calculator.dto.Violation;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationErrorResponseTest {

    @Test
    void testViolationGetters() {
        Violation violation = new Violation("field", "Ошибка");

        assertEquals("field", violation.getFieldName());
        assertEquals("Ошибка", violation.getMessage());
    }

    @Test
    void testValidationErrorResponseGetters() {
        Violation v1 = new Violation("field1", "msg1");
        Violation v2 = new Violation("field2", "msg2");

        ValidationErrorResponse response = new ValidationErrorResponse(List.of(v1, v2));

        assertNotNull(response.getViolations());
        assertEquals(2, response.getViolations().size());
        assertEquals("field1", response.getViolations().get(0).getFieldName());
        assertEquals("msg2", response.getViolations().get(1).getMessage());
    }
}