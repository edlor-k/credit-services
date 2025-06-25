package ru.creditservices.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
            JsonNode node = objectMapper.readTree(response);
            if (node.isArray()) {
                return objectMapper.readValue(response, new TypeReference<>() {});
            }
            if (node.has("violations")) {
                ValidationErrorResponse error = objectMapper.treeToValue(node, ValidationErrorResponse.class);
                throw new CalculatorValidationException(
                        CalculatorErrorType.REQUEST_ERROR,
                        error.getViolations()
                );
            }
            throw new CalculatorValidationException(CalculatorErrorType.REQUEST_ERROR, null);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при парсинге ответа калькулятора /offers: {}", response, e);
            throw new ParseCalculatorException("Ошибка при парсинге ответа калькулятора /offers: " + response);
        }
    }

    @Override
    public CalculatorResult parseCalculatorResultResponse(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            if (node.has("violations")) {
                ValidationErrorResponse error = objectMapper.treeToValue(node, ValidationErrorResponse.class);
                if (!error.getViolations().isEmpty()) {
                    String field = error.getViolations().getFirst().getFieldName();
                    if ("business".equals(field)) {
                        return CalculatorResult.businessDecline(
                                error.getViolations().getFirst().getMessage(),
                                error.getViolations()
                        );
                    } else if ("request".equals(field)) {
                        return CalculatorResult.requestError(
                                error.getViolations().getFirst().getMessage(),
                                error.getViolations()
                        );
                    }
                }
                return CalculatorResult.requestError(
                        "Неизвестная ошибка калькулятора",
                        error.getViolations()
                );
            }
            CreditDto creditDto = objectMapper.treeToValue(node, CreditDto.class);
            return CalculatorResult.approved(creditDto);
        } catch (Exception e) {
            log.error("Ошибка при парсинге ответа калькулятора /calc: {}", response, e);
            throw new ParseCalculatorException("Ошибка при парсинге ответа калькулятора /calc: " + response);
        }
    }
}
