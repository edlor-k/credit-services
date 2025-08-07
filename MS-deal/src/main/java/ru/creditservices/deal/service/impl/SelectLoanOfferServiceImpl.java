package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.model.entity.LoanOfferEntity;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.service.KafkaEmailService;
import ru.creditservices.deal.service.SelectLoanOfferService;
import ru.creditservices.deal.service.StatementManagerService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SelectLoanOfferServiceImpl implements SelectLoanOfferService {

    private final LoanOfferMapper loanOfferMapper;
    private final StatementManagerService statementManagerService;
    private final EmailMessageFactory emailMessageFactory;
    private final KafkaEmailService kafkaEmailService;

    @Override
    @Transactional
    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        log.info("Selecting loan offer for statementId={}", loanOfferDto.getStatementId());

        LoanOfferEntity loanOfferEntity = loanOfferMapper.toEntity(loanOfferDto);
        statementManagerService.selectLoanOfferToStatement(loanOfferEntity);

        notifyClientByEmail(loanOfferDto.getStatementId());

        log.info("Loan offer selection completed for statementId={}", loanOfferDto.getStatementId());
    }

    private void notifyClientByEmail(UUID statementId) {
        EmailMessageDto emailMessage = emailMessageFactory.buildEmailMessage(statementId,
                EmailTheme.FINISH_REGISTRATION);
        kafkaEmailService.sendMessage(emailMessage);
        log.debug("Email notification sent for statementId={}, theme={}", statementId,
                EmailTheme.FINISH_REGISTRATION);
    }
}
