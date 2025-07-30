package ru.creditservices.statement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.statement.dto.LoanOfferDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectLoanOfferServiceImplTest {

    @Mock
    private DealClientServiceImpl dealClientService;

    @InjectMocks
    private SelectLoanOfferServiceImpl selectLoanOfferService;

    @Test
    void selectLoanOfferDelegatesToDealClientService() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        selectLoanOfferService.selectLoanOffer(loanOfferDto);
        verify(dealClientService, times(1)).selectLoanOffer(loanOfferDto);
    }
}
