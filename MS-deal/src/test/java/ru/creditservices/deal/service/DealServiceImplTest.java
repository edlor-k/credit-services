package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.service.impl.DealServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock private CalculateFinalParametersService calculateFinalParametersService;
    @Mock private CreateLoanStatementService createLoanStatementService;
    @Mock private SelectLoanOfferService selectLoanOfferService;
    @Mock private DocumentsService documentsService;
    @Mock private AdminService adminService;
    @Mock private StatementManagerService statementManagerService;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    @DisplayName("createLoanStatement -> делегирует в CreateLoanStatementService и возвращает список офферов")
    void createLoanStatement_delegatesAndReturns() {
        LoanStatementRequestDto req = new LoanStatementRequestDto();
        List<LoanOfferDto> offers = List.of(new LoanOfferDto(), new LoanOfferDto());
        when(createLoanStatementService.getLoanOffers(req)).thenReturn(offers);

        List<LoanOfferDto> result = dealService.createLoanStatement(req);

        assertThat(result).isSameAs(offers);
        verify(createLoanStatementService).getLoanOffers(req);
        verifyNoMoreInteractions(createLoanStatementService, selectLoanOfferService, documentsService,
                adminService, calculateFinalParametersService, statementManagerService);
    }

    @Test
    @DisplayName("selectLoanOffer -> сначала выбирает оффер, затем отправляет запрос на завершение регистрации")
    void selectLoanOffer_callsSelectAndSendsFinishRegistration() {
        LoanOfferDto dto = new LoanOfferDto();
        UUID id = UUID.randomUUID();
        dto.setStatementId(id);

        dealService.selectLoanOffer(dto);

        InOrder inOrder = inOrder(selectLoanOfferService, documentsService);
        inOrder.verify(selectLoanOfferService).selectLoanOffer(dto);
        inOrder.verify(documentsService).sendFinishRegistrationRequest(id);
        verifyNoMoreInteractions(selectLoanOfferService, documentsService);
    }

    @Test
    @DisplayName("calculateFinalLoanParameters -> считает параметры и шлёт CREATE_DOCUMENTS")
    void calculateFinalLoanParameters_calculatesAndSendsCreateDocuments() {
        UUID id = UUID.randomUUID();
        FinishRegistrationRequestDto dto = new FinishRegistrationRequestDto();

        dealService.calculateFinalLoanParameters(id, dto);

        InOrder inOrder = inOrder(calculateFinalParametersService, documentsService);
        inOrder.verify(calculateFinalParametersService).calculateFinalParameters(id, dto);
        inOrder.verify(documentsService).sendCreateDocumentsRequest(id);
        verifyNoMoreInteractions(calculateFinalParametersService, documentsService);
    }

    @Test
    @DisplayName("sendDocuments -> готовит документы и шлёт SEND_DOCUMENTS info")
    void sendDocuments_preparesAndSendsInfo() {
        UUID id = UUID.randomUUID();

        dealService.sendDocuments(id);

        InOrder inOrder = inOrder(statementManagerService, documentsService);
        inOrder.verify(statementManagerService).prepareDocuments(id);
        inOrder.verify(documentsService).sendDocumentsSendInfo(id);
        verifyNoMoreInteractions(statementManagerService, documentsService);
    }

    @Test
    @DisplayName("requestToSignDocuments -> делегирует в DocumentsService")
    void requestToSignDocuments_delegates() {
        UUID id = UUID.randomUUID();

        dealService.requestToSignDocuments(id);

        verify(documentsService).requestToSignDocuments(id);
        verifyNoMoreInteractions(documentsService);
    }

    @Test
    @DisplayName("confirmDocumentSigning -> делегирует в DocumentsService.documentsSigned")
    void confirmDocumentSigning_delegates() {
        UUID id = UUID.randomUUID();
        String code = "123456";

        dealService.confirmDocumentSigning(id, code);

        verify(documentsService).documentsSigned(id, code);
        verifyNoMoreInteractions(documentsService);
    }

    @Test
    @DisplayName("getAllStatements -> делегирует в AdminServiceImpl и возвращает список")
    void getAllStatements_delegatesToAdminService() {
        List<StatementDto> expected = List.of(new StatementDto(), new StatementDto());
        when(adminService.getAllStatements()).thenReturn(expected);

        List<StatementDto> result = dealService.getAllStatements();

        assertThat(result).isSameAs(expected);
        verify(adminService).getAllStatements();
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getStatementById -> делегирует в AdminServiceImpl и возвращает DTO")
    void getStatementById_delegatesToAdminService() {
        UUID id = UUID.randomUUID();
        StatementDto dto = new StatementDto();
        when(adminService.getStatementById(id)).thenReturn(dto);

        StatementDto result = dealService.getStatementById(id);

        assertThat(result).isSameAs(dto);
        verify(adminService).getStatementById(id);
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("updateStatementStatus -> делегирует в DocumentsService")
    void updateStatementStatus_delegatesToDocumentsService() {
        UUID id = UUID.randomUUID();
        String status = "DOCUMENT_CREATED";

        dealService.updateStatementStatus(id, status);

        verify(documentsService).updateStatementStatus(id, status);
        verifyNoMoreInteractions(documentsService);
    }
}
