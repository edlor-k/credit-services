package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.model.entity.CreditEntity;
import ru.creditservices.deal.model.enums.CreditStatus;
import ru.creditservices.deal.repository.CreditRepository;
import ru.creditservices.deal.service.impl.CreditManagerServiceImpl;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditManagerServiceImplTest {

    @Mock
    private CreditRepository creditRepository;

    @InjectMocks
    private CreditManagerServiceImpl creditManagerService;

    private CreditEntity creditEntity;

    @BeforeEach
    void setUp() {
        creditEntity = CreditEntity.builder()
                .creditId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9000))
                .rate(BigDecimal.valueOf(12.5))
                .psk(BigDecimal.valueOf(13.1))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .creditStatus(null)
                .build();
    }

    @Test
    @DisplayName("Создание кредита из CreditEntity должно установить статус и сохранить в репозитории")
    void testCreateCreditFromCreditEntityShouldSetStatusAndSave() {
        creditManagerService.createCreditFromCreditEntity(creditEntity);

        assertEquals(CreditStatus.CALCULATED, creditEntity.getCreditStatus());

        verify(creditRepository, times(1)).save(creditEntity);
    }
}