package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.model.entity.CreditEntity;
import ru.creditservices.deal.model.enums.CreditStatus;
import ru.creditservices.deal.repository.CreditRepository;
import ru.creditservices.deal.service.CreditManagerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditManagerServiceImpl implements CreditManagerService {

    private final CreditRepository creditRepository;

    @Override
    @Transactional
    public void createCreditFromCreditEntity(CreditEntity creditEntity) {
        creditEntity.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(creditEntity);
    }
}
