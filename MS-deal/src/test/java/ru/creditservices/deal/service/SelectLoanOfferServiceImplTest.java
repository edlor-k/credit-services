package ru.creditservices.deal.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.model.entity.LoanOfferEntity;
import ru.creditservices.deal.service.impl.SelectLoanOfferServiceImpl;

import static org.mockito.Mockito.*;

class SelectLoanOfferServiceImplTest {

    @Mock
    private LoanOfferMapper loanOfferMapper;

    @Mock
    private StatementManagerService statementManagerService;

    @InjectMocks
    private SelectLoanOfferServiceImpl selectLoanOfferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(loanOfferMapper, statementManagerService);
    }

    @Test
    void selectLoanOffer_shouldCallMapperAndManager() {
        LoanOfferDto dto = LoanOfferDto.builder()
                .statementId(java.util.UUID.randomUUID())
                .build();
        LoanOfferEntity entity = LoanOfferEntity.builder()
                .statementId(dto.getStatementId())
                .build();

        when(loanOfferMapper.toEntity(dto)).thenReturn(entity);
        selectLoanOfferService.selectLoanOffer(dto);
        verify(loanOfferMapper).toEntity(dto);
        verify(statementManagerService).selectLoanOfferToStatement(entity);
    }
}