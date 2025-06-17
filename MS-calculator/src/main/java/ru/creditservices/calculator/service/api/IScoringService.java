package ru.creditservices.calculator.service.api;

import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.ScoringDataDto;

public interface IScoringService {
    CreditDto getFinalCreditInfo(ScoringDataDto scoringData);
}
