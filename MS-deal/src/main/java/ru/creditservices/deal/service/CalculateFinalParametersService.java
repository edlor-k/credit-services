package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.FinishRegistrationRequestDto;

import java.util.UUID;

public interface CalculateFinalParametersService {
    void calculateFinalParameters(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);
}
