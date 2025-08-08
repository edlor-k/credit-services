package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.service.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final CalculateFinalParametersService calculateFinalParametersService;
    private final CreateLoanStatementService createLoanStatementService;
    private final SelectLoanOfferService selectLoanOfferService;
    private final DocumentsService documentsService;
    private final AdminServiceImpl adminService;

    @Override
    public List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto dto) {
        return createLoanStatementService.getLoanOffers(dto);
    }

    @Override
    public void selectLoanOffer(LoanOfferDto dto) {
        selectLoanOfferService.selectLoanOffer(dto);
    }

    @Override
    public void calculateFinalLoanParameters(UUID statementId, FinishRegistrationRequestDto dto) {
        calculateFinalParametersService.calculateFinalParameters(statementId, dto);
        documentsService.finishRegistrationInfo(statementId);
    }

    @Override
    public void sendDocuments(UUID statementId) {
        documentsService.prepareDocumentsForSigning(statementId);
    }

    @Override
    public void requestToSignDocuments(UUID statementId) {
        documentsService.requestToSignDocuments(statementId);
    }

    @Override
    public void confirmDocumentSigning(UUID statementId, String sesCode) {
        documentsService.documentsSigned(statementId, sesCode);
    }

    @Override
    public List<StatementDto> getAllStatements() {
        return adminService.getAllStatements();
    }

    @Override
    public StatementDto getStatementById(UUID statementId) {
        return adminService.getStatementById(statementId);
    }
}
