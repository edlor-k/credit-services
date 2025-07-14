package ru.creditservices.statement.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.service.GetLoanOffersService;
import ru.creditservices.statement.service.SelectLoanOfferService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementControllerTest {

    @Mock
    private GetLoanOffersService getLoanOffersService;

    @Mock
    private SelectLoanOfferService selectLoanOfferService;

    @InjectMocks
    private StatementController controller;

    @Test
    void getLoanOffers_shouldReturnLoanOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> offers = Arrays.asList(offer1, offer2);

        when(getLoanOffersService.getLoanOffers(request)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = controller.getLoanOffers(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(offer1, offer2);
        verify(getLoanOffersService, times(1)).getLoanOffers(request);
    }

    @Test
    void selectLoanOffer_shouldCallServiceAndReturnOk() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        ResponseEntity<Void> response = controller.selectLoanOffer(loanOfferDto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
        verify(selectLoanOfferService, times(1)).selectLoanOffer(loanOfferDto);
    }
}
