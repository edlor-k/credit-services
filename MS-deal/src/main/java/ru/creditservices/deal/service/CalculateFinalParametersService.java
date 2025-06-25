package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.FinishRegistrationRequestDto;

public interface CalculateFinalParametersService {
    void calculateFinalParameters(String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);
}
