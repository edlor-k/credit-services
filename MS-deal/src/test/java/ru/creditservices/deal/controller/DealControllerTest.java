package ru.creditservices.deal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.service.CalculateFinalParametersService;
import ru.creditservices.deal.service.CreateLoanStatementService;
import ru.creditservices.deal.service.SelectLoanOfferService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private CalculateFinalParametersService calculateFinalParametersService;

    @Mock
    private CreateLoanStatementService createLoanStatementService;

    @Mock
    private SelectLoanOfferService selectLoanOfferService;

    @InjectMocks
    private DealController dealController;

    private LoanStatementRequestDto statementRequestDto;
    private LoanOfferDto loanOfferDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;

    @BeforeEach
    void setUp() {
        statementRequestDto = new LoanStatementRequestDto();
        loanOfferDto = new LoanOfferDto();
        finishRegistrationRequestDto = new FinishRegistrationRequestDto();
    }

    @Test
    @DisplayName("Корректное создание заявки на кредит и получение списка предложений")
    void createLoanStatementShouldReturnLoanOfferList() {
        List<LoanOfferDto> offers = List.of(loanOfferDto, new LoanOfferDto());
        when(createLoanStatementService.getLoanOffers(statementRequestDto)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = dealController.createLoanStatement(statementRequestDto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2);
        verify(createLoanStatementService, times(1)).getLoanOffers(statementRequestDto);
    }

    @Test
    @DisplayName("Корректный выбор предложения по кредиту")
    void selectLoanOfferShouldCallServiceAndReturnOk() {
        doNothing().when(selectLoanOfferService).selectLoanOffer(loanOfferDto);

        ResponseEntity<Void> response = dealController.selectLoanOffer(loanOfferDto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(selectLoanOfferService, times(1)).selectLoanOffer(loanOfferDto);
    }

    @Test
    @DisplayName("Корректный расчет окончательных параметров кредита")
    void calculateFinalLoanParametersShouldCallServiceAndReturnOk() {
        String statementId = "123";
        doNothing().when(calculateFinalParametersService)
                .calculateFinalParameters(statementId, finishRegistrationRequestDto);

        ResponseEntity<Void> response = dealController.calculateFinalLoanParameters(statementId, finishRegistrationRequestDto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(calculateFinalParametersService, times(1))
                .calculateFinalParameters(statementId, finishRegistrationRequestDto);
    }
}