package ru.creditservices.calculator.service;

import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.ScoringDataDto;

public interface ScoringService {
    CreditDto getFinalCreditInfo(ScoringDataDto scoringData);
}
