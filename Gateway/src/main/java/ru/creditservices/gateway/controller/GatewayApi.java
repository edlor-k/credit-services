package ru.creditservices.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;
import ru.creditservices.gateway.dto.StatementDto;

import java.util.List;
import java.util.UUID;

public interface GatewayApi {

    @Operation(summary = "Получить предложения по займу")
    ResponseEntity<List<LoanOfferDto>> fetchLoanOffers(LoanStatementRequestDto request);

    @Operation(summary = "Выбрать предложение по займу")
    ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOfferDto);

    @Operation(summary = "Завершить регистрацию клиента")
    ResponseEntity<Void> finishRegistration(FinishRegistrationRequestDto request,
                                            @Parameter(description = "Идентификатор заявки") UUID statementId);

    @Operation(summary = "Создать документы по заявке")
    ResponseEntity<Void> createDocuments(@Parameter(description = "Идентификатор заявки") UUID statementId);

    @Operation(summary = "Подписать документы")
    ResponseEntity<Void> signDocuments(@Parameter(description = "Идентификатор заявки") UUID statementId);

    @Operation(summary = "Подтвердить документы по коду из СМС")
    ResponseEntity<Void> verifyDocuments(
            @Parameter(description = "Идентификатор заявки") UUID statementId,
            @Parameter(description = "Код подтверждения") String sesCode);

    @Operation(summary = "Получить список всех заявок")
    ResponseEntity<List<StatementDto>> fetchStatements();

    @Operation(summary = "Получить заявку по ID")
    ResponseEntity<StatementDto> fetchStatementById(
            @Parameter(description = "Идентификатор заявки") UUID statementId);
}
