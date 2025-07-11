package ru.creditservices.deal.service;

import ru.creditservices.deal.model.entity.CreditEntity;

public interface CreditManagerService {
    void createCreditFromCreditEntity(CreditEntity creditEntity);
}
