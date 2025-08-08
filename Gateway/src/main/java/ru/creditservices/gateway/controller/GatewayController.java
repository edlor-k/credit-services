package ru.creditservices.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;
import ru.creditservices.gateway.dto.StatementDto;
import ru.creditservices.gateway.service.GatewayService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class GatewayController implements GatewayApi {

    private final GatewayService gatewayService;

    @PostMapping("/statements")
    @Override
    public ResponseEntity<List<LoanOfferDto>> fetchLoanOffers(@RequestBody LoanStatementRequestDto request) {
        log.info("Получен запрос на предложения по займу");
        log.debug("Параметры запроса: {}", request);
        List<LoanOfferDto> offers = gatewayService.fetchLoanOffers(request);
        log.debug("Получено {} предложений", offers.size());
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/statements/select")
    @Override
    public ResponseEntity<Void> selectLoanOffer(@RequestBody LoanOfferDto loanOfferDto) {
        log.info("Запрос на выбор предложения");
        log.debug("Детали предложения: {}", loanOfferDto);
        gatewayService.selectLoanOffer(loanOfferDto);
        log.info("Предложение успешно выбрано");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/statements/registration/{statementId}")
    @Override
    public ResponseEntity<Void> finishRegistration(@RequestBody FinishRegistrationRequestDto request,
                                                   @PathVariable UUID statementId) {
        log.info("Запрос на завершение регистрации. ID заявки: {}", statementId);
        log.debug("Детали регистрации: {}", request);
        gatewayService.finishRegistration(request, statementId);
        log.info("Регистрация по заявке {} завершена", statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/statements/{statementId}/documents")
    @Override
    public ResponseEntity<Void> createDocuments(@PathVariable UUID statementId) {
        log.info("Создание документов по заявке {}", statementId);
        gatewayService.createDocuments(statementId);
        log.info("Документы по заявке {} успешно созданы", statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/statements/{statementId}/documents/sign")
    @Override
    public ResponseEntity<Void> signDocuments(@PathVariable UUID statementId) {
        log.info("Запрос на подписание документов по заявке {}", statementId);
        gatewayService.signDocuments(statementId);
        log.info("Документы по заявке {} успешно подписаны", statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/statements/{statementId}/documents/verify/{sesCode}")
    @Override
    public ResponseEntity<Void> verifyDocuments(@PathVariable UUID statementId,
                                                @PathVariable String sesCode) {
        log.info("Подтверждение документов по заявке {}", statementId);
        log.debug("Код подтверждения: {}", sesCode);
        gatewayService.verifyDocuments(statementId, sesCode);
        log.info("Документы по заявке {} успешно подтверждены", statementId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statements")
    @Override
    public ResponseEntity<List<StatementDto>> fetchStatements() {
        log.info("Запрос списка заявок");
        List<StatementDto> statements = gatewayService.fetchStatements();
        log.debug("Количество заявок: {}", statements.size());
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/statements/{statementId}")
    @Override
    public ResponseEntity<StatementDto> fetchStatementById(@PathVariable UUID statementId) {
        log.info("Запрос на получение заявки по ID: {}", statementId);
        StatementDto dto = gatewayService.fetchStatementById(statementId);
        if (dto == null) {
            log.warn("Заявка с ID {} не найдена", statementId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Детали заявки: {}", dto);
        return ResponseEntity.ok(dto);
    }
}
