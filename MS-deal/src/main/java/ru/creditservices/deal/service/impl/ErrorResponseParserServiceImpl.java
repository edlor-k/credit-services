package ru.creditservices.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.exception.CalculatorValidationException;
import ru.creditservices.deal.exception.ParseCalculatorException;
import ru.creditservices.deal.model.enums.CalculatorErrorType;
import ru.creditservices.deal.service.ErrorResponseParserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorResponseParserServiceImpl implements ErrorResponseParserService {
    private final ObjectMapper objectMapper;

    @Override
    public List<LoanOfferDto> parseLoanOffersResponse(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            try {
                ValidationErrorResponse error = objectMapper.readValue(response, ValidationErrorResponse.class);
                log.warn("Loan offer response contains violations: {}", error.getViolations());
                throw new CalculatorValidationException(
                        CalculatorErrorType.REQUEST_ERROR,
                        error.getViolations()
                );
            } catch (JsonProcessingException ex) {
                log.error("Failed to parse calculator response for /offers", ex);
                throw new ParseCalculatorException("Ошибка парсинга ответа от калькулятора /offers");
            }
        }
    }

    @Override
    public CalculatorResult parseCalculatorResultResponse(String response) {
        try {
            return CalculatorResult.approved(objectMapper.readValue(response, CreditDto.class));
        } catch (JsonProcessingException e) {
            try {
                ValidationErrorResponse error = objectMapper.readValue(response, ValidationErrorResponse.class);
                if (!error.getViolations().isEmpty()) {
                    String field = error.getViolations().getFirst().getFieldName();
                    if ("business".equals(field)) {
                        log.info("Calculator declined business logic: {}", error.getViolations());
                        return CalculatorResult.businessDecline(
                                error.getViolations().getFirst().getMessage(),
                                error.getViolations()
                        );
                    } else if ("request".equals(field)) {
                        log.warn("Calculator validation failed: {}", error.getViolations());
                        return CalculatorResult.requestError(
                                error.getViolations().getFirst().getMessage(),
                                error.getViolations()
                        );
                    }
                }
                log.warn("Unknown calculator error in /calc: {}", error.getViolations());
                return CalculatorResult.requestError(
                        "Неизвестная ошибка калькулятора",
                        error.getViolations()
                );
            } catch (JsonProcessingException ex) {
                log.error("Failed to parse calculator response for /calc", ex);
                throw new ParseCalculatorException("Ошибка парсинга ответа от калькулятора /calc");
            }
        }
    }

}
