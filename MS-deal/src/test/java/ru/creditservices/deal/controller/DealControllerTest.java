package ru.creditservices.deal.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.service.DealService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private DealService dealService;

    @InjectMocks
    private DealController dealController;

    @Test
    @DisplayName("Корректное создание заявки на кредит и получение списка предложений")
    void createLoanStatementShouldReturnLoanOfferList() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> offers = List.of(offer1, offer2);

        when(dealService.createLoanStatement(request)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = dealController.createLoanStatement(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(offer1, offer2);
        verify(dealService, times(1)).createLoanStatement(request);
    }

    @Test
    @DisplayName("Корректный выбор предложения по кредиту")
    void selectLoanOfferShouldCallServiceAndReturnOk() {
        LoanOfferDto dto = new LoanOfferDto();

        doNothing().when(dealService).selectLoanOffer(dto);

        ResponseEntity<Void> response = dealController.selectLoanOffer(dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService, times(1)).selectLoanOffer(dto);
    }

    @Test
    @DisplayName("Расчет финальных параметров кредита")
    void calculateFinalLoanParametersShouldDelegateToService() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto dto = new FinishRegistrationRequestDto();

        doNothing().when(dealService).calculateFinalLoanParameters(statementId, dto);

        ResponseEntity<Void> response = dealController.calculateFinalLoanParameters(statementId, dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService).calculateFinalLoanParameters(statementId, dto);
    }

    @Test
    @DisplayName("Отправка документов клиенту")
    void requestToSendDocumentsShouldDelegateToService() {
        UUID statementId = UUID.randomUUID();

        doNothing().when(dealService).sendDocuments(statementId);

        ResponseEntity<Void> response = dealController.requestToSendDocuments(statementId);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService).sendDocuments(statementId);
    }

    @Test
    @DisplayName("Запрос на подписание документов")
    void requestToSignDocumentsShouldDelegateToService() {
        UUID statementId = UUID.randomUUID();

        doNothing().when(dealService).requestToSignDocuments(statementId);

        ResponseEntity<Void> response = dealController.requestToSignDocuments(statementId);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService).requestToSignDocuments(statementId);
    }

    @Test
    @DisplayName("Подтверждение подписания документов")
    void confirmDocumentSigningShouldDelegateToService() {
        UUID statementId = UUID.randomUUID();
        String code = "123456";

        doNothing().when(dealService).confirmDocumentSigning(statementId, code);

        ResponseEntity<Void> response = dealController.confirmDocumentSigning(statementId, code);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService).confirmDocumentSigning(statementId, code);
    }
}
