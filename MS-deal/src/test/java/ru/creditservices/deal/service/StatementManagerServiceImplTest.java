package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.exception.InvalidApplicationStatus;
import ru.creditservices.deal.exception.LoanOfferAlreadyExist;
import ru.creditservices.deal.exception.StatementAlreadyExistException;
import ru.creditservices.deal.exception.StatementNotFoundException;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.repository.StatementRepository;
import ru.creditservices.deal.service.impl.StatementManagerServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementManagerServiceImplTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private StatementManagerServiceImpl statementManagerService;

    private ClientEntity client;
    private StatementEntity statement;

    @BeforeEach
    void setup() {
        client = ClientEntity.builder()
                .clientId(UUID.randomUUID())
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        statement = StatementEntity.builder()
                .statementId(UUID.randomUUID())
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Корректное создание начальной заявки для клиента")
    void createStatementFromClientWhenStatementNotExistCreatesNew() {
        when(statementRepository.findStatementEntityByClient(client)).thenReturn(Optional.empty());
        when(statementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatementEntity created = statementManagerService.createStatementFromClient(client);

        assertNotNull(created);
        assertEquals(client, created.getClient());
        assertEquals(ApplicationStatus.PREAPPROVAL, created.getStatus());
        assertFalse(created.getStatusHistory().isEmpty());

        verify(statementRepository).save(any());
    }

    @Test
    @DisplayName("Выбрасывается исключение, если для пользователю уже существует заявка")
    void createStatementFromClientWhenStatementExistsThrowsException() {
        when(statementRepository.findStatementEntityByClient(client)).thenReturn(Optional.of(statement));
        assertThrows(StatementAlreadyExistException.class,
                () -> statementManagerService.createStatementFromClient(client));
    }

    @Test
    @DisplayName("Корректное обновление статуса заявки")
    void selectLoanOfferToStatementHappyPath() {
        UUID stId = statement.getStatementId();
        LoanOfferEntity offer = LoanOfferEntity.builder().statementId(stId).build();
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setAppliedOffer(null);

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        when(statementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        statementManagerService.selectLoanOfferToStatement(offer);

        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());
        assertEquals(offer, statement.getAppliedOffer());
        assertTrue(statement.getStatusHistory().stream().anyMatch(
                el -> el.getStatus() == ApplicationStatus.APPROVED
        ));
        verify(statementRepository).save(statement);
    }

    @Test
    @DisplayName("Выбрасывается исключение, если статус заявки не PREAPPROVAL")
    void selectLoanOfferToStatementWhenStatusNotPreapprovalThrowsException() {
        UUID stId = statement.getStatementId();
        LoanOfferEntity offer = LoanOfferEntity.builder().statementId(stId).build();
        statement.setStatus(ApplicationStatus.APPROVED);

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        assertThrows(InvalidApplicationStatus.class,
                () -> statementManagerService.selectLoanOfferToStatement(offer));
    }

    @Test
    @DisplayName("Выбрасывается исключение, если заявка уже содержит выбранное предложение")
    void selectLoanOfferToStatementWhenOfferAlreadyExistsThrowsException() {
        UUID stId = statement.getStatementId();
        LoanOfferEntity offer = LoanOfferEntity.builder().statementId(stId).build();
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setAppliedOffer(LoanOfferEntity.builder().build());

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        assertThrows(LoanOfferAlreadyExist.class,
                () -> statementManagerService.selectLoanOfferToStatement(offer));
    }

    @Test
    @DisplayName("Корректное обновление заявки из данных скоринга")
    void updateStatementFromScoringDataHappyPath() {
        UUID stId = statement.getStatementId();
        statement.setStatus(ApplicationStatus.APPROVED);
        ScoringDataEntity scoringData = ScoringDataEntity.builder().build();

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        when(statementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        statementManagerService.updateStatementFromScoringData(scoringData, stId);

        assertEquals(ApplicationStatus.CC_APPROVED, statement.getStatus());
        assertTrue(statement.getStatusHistory().stream().anyMatch(
                el -> el.getStatus() == ApplicationStatus.CC_APPROVED
        ));
        verify(statementRepository).save(statement);
    }

    @Test
    @DisplayName("Выбрасывается исключение, если статус заявки не APPROVED или CC_DENIED")
    void updateStatementFromScoringDataWhenInvalidStatusThrowsException() {
        UUID stId = statement.getStatementId();
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        ScoringDataEntity scoringData = ScoringDataEntity.builder().build();

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        assertThrows(InvalidApplicationStatus.class,
                () -> statementManagerService.updateStatementFromScoringData(scoringData, stId));
    }

    @Test
    @DisplayName("Добавление кредита к заявке успешно")
    void addCreditToStatementHappyPath() {
        UUID stId = statement.getStatementId();
        CreditEntity credit = CreditEntity.builder().build();

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        when(statementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        statementManagerService.addCreditToStatement(stId, credit);

        assertEquals(credit, statement.getCredit());
        verify(statementRepository).save(statement);
    }

    @Test
    @DisplayName("Выбрасывается исключение, если заявка не найдена при добавлении кредита")
    void setLoanWaiverHappyPath() {
        UUID stId = statement.getStatementId();
        statement.setStatus(ApplicationStatus.APPROVED);

        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));
        when(statementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        statementManagerService.setLoanWaiver(stId);

        assertEquals(ApplicationStatus.CC_DENIED, statement.getStatus());
        assertTrue(statement.getStatusHistory().stream().anyMatch(
                el -> el.getStatus() == ApplicationStatus.CC_DENIED
        ));
        verify(statementRepository).save(statement);
    }

    @Test
    @DisplayName("Успешный поиск заявки по ID")
    void getStatementOrThrowFound() {
        UUID stId = statement.getStatementId();
        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.of(statement));

        StatementEntity found = statementManagerService.getStatementOrThrow(stId);
        assertEquals(statement, found);
    }

    @Test
    @DisplayName("Выбрасывается исключение, если заявка не найдена по ID")
    void getStatementOrThrowNotFoundThrowsException() {
        UUID stId = UUID.randomUUID();
        when(statementRepository.findStatementEntityByStatementId(stId)).thenReturn(Optional.empty());
        assertThrows(StatementNotFoundException.class, () -> statementManagerService.getStatementOrThrow(stId));
    }
}