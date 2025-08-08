package ru.creditservices.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;
import ru.creditservices.gateway.dto.StatementDto;
import ru.creditservices.gateway.service.DealClientService;
import ru.creditservices.gateway.service.GatewayService;
import ru.creditservices.gateway.service.StatementClientService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayServiceImpl extends BaseRestClient implements GatewayService {

    private final DealClientService dealClientService;
    private final StatementClientService statementClientService;

    @Override
    public List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto request) {
        log.info("GatewayService: получение предложений по займу");
        log.debug("Параметры запроса: {}", request);
        return statementClientService.fetchLoanOffers(request);
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        log.info("GatewayService: выбор предложения по займу");
        log.debug("Выбранное предложение: {}", loanOfferDto);
        statementClientService.selectLoanOffer(loanOfferDto);
    }

    @Override
    public void finishRegistration(FinishRegistrationRequestDto request, UUID statementId) {
        log.info("GatewayService: завершение регистрации по заявке {}", statementId);
        log.debug("Данные регистрации: {}", request);
        dealClientService.finishRegistration(request, statementId);
    }

    @Override
    public void createDocuments(UUID statementId) {
        log.info("GatewayService: создание документов по заявке {}", statementId);
        dealClientService.createDocuments(statementId);
    }

    @Override
    public void signDocuments(UUID statementId) {
        log.info("GatewayService: подписание документов по заявке {}", statementId);
        dealClientService.signDocuments(statementId);
    }

    @Override
    public void verifyDocuments(UUID statementId, String sesCode) {
        log.info("GatewayService: подтверждение документов по заявке {}, код: {}", statementId, sesCode);
        dealClientService.verifyDocuments(statementId, sesCode);
    }

    @Override
    public List<StatementDto> fetchStatements() {
        log.info("GatewayService: получение всех заявок");
        return dealClientService.fetchStatements();
    }

    @Override
    public StatementDto fetchStatementById(UUID statementId) {
        log.info("GatewayService: получение заявки по ID: {}", statementId);
        return dealClientService.fetchStatementById(statementId);
    }

}
