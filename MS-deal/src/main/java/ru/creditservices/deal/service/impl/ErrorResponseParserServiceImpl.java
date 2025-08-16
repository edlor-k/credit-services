package ru.creditservices.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.ErrorResponseDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.exception.CalculatorServiceException;
import ru.creditservices.deal.exception.ParseCalculatorException;
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
        } catch (JsonProcessingException ignore) { }

        ErrorResponseDto err = readErrorOrThrow(response, "/offers");
        log.warn("Calculator /offers error: {}", err);
        throw new CalculatorServiceException(err);
    }

    @Override
    public CreditDto parseCalculatorResultResponse(String response) {
        try {
            return objectMapper.readValue(response, CreditDto.class);
        } catch (JsonProcessingException ignore) { }

        ErrorResponseDto err = readErrorOrThrow(response, "/calc");
        log.warn("Calculator /calc error: {}", err);
        throw new CalculatorServiceException(err);
    }

    private ErrorResponseDto readErrorOrThrow(String raw, String endpoint) {
        try {
            return objectMapper.readValue(raw, ErrorResponseDto.class);
        } catch (Exception ex) {
            log.error("Failed to parse calculator error for {}: {}", endpoint, raw, ex);
            throw new ParseCalculatorException("Ошибка парсинга ответа от калькулятора " + endpoint);
        }
    }
}
