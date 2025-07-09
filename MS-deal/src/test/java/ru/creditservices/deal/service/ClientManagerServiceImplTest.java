package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.exception.ClientAlreadyExistException;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;
import ru.creditservices.deal.repository.ClientRepository;
import ru.creditservices.deal.service.impl.ClientManagerServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientManagerServiceImplTest {

    @InjectMocks
    private ClientManagerServiceImpl clientManagerService;

    @Mock
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Создание клиента из запроса на кредитный отчет должно создавать нового клиента, " +
            "если он не существует")
    void createClientFromLoanStatementRequestShouldCreateClientWhenNotExists() {
        LoanStatementEntity entity = LoanStatementEntity.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Ivanovich")
                .birthdate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        when(clientRepository.findByPassportId_SeriesAndPassportId_Number("1234", "567890"))
                .thenReturn(Optional.empty());

        ClientEntity saved = ClientEntity.builder()
                .firstName("Ivan").lastName("Petrov").middleName("Ivanovich")
                .birthdate(LocalDate.of(1990,1,1))
                .email("test@example.com")
                .passportId(PassportEntity.builder().series("1234").number("567890").build())
                .build();

        when(clientRepository.save(any(ClientEntity.class))).thenReturn(saved);

        ClientEntity result = clientManagerService.createClient(entity);

        assertNotNull(result);
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("Ivanovich", result.getMiddleName());
        assertEquals("1234", result.getPassportId().getSeries());
        assertEquals("567890", result.getPassportId().getNumber());
        verify(clientRepository).findByPassportId_SeriesAndPassportId_Number("1234", "567890");
        verify(clientRepository).save(any(ClientEntity.class));
    }

    @Test
    @DisplayName("Создание клиента из запроса на кредитный отчет должно выбрасывать исключение, " +
            "если клиент уже существует")
    void createClientFromLoanStatementRequestShouldThrowWhenClientExists() {
        LoanStatementEntity entity = LoanStatementEntity.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Ivanovich")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        when(clientRepository.findByPassportId_SeriesAndPassportId_Number("1234", "567890"))
                .thenReturn(Optional.of(mock(ClientEntity.class)));

        assertThrows(ClientAlreadyExistException.class, () ->
                clientManagerService.createClient(entity)
        );
        verify(clientRepository).findByPassportId_SeriesAndPassportId_Number("1234", "567890");
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Обновление информации клиента из данных скоринга")
    void updateClientInformationFromScoringDataShouldUpdateAndSave() {
        ScoringDataEntity scoring = ScoringDataEntity.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Ivanovich")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .birthdate(LocalDate.of(1991, 2, 2))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2010, 10, 10))
                .passportIssueBranch("MVD Russia")
                .dependentAmount(2)
                .employment(EmploymentEntity.builder().build())
                .build();

        PassportEntity passportEntity = PassportEntity.builder().series("1234").number("567890").build();
        ClientEntity client = ClientEntity.builder()
                .passportId(passportEntity).build();

        when(clientRepository.findByPassportId_SeriesAndPassportId_Number("1234", "567890"))
                .thenReturn(Optional.of(client));
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(client);

        clientManagerService.updateClientInformationFromScoringData(scoring);

        assertEquals(LocalDate.of(1991, 2, 2), client.getBirthdate());
        assertEquals(Gender.MALE, client.getGender());
        assertEquals(MaritalStatus.SINGLE, client.getMaritalStatus());
        assertEquals("Ivanovich", client.getMiddleName());
        assertEquals(2, client.getDependentAmount());
        assertEquals(LocalDate.of(2010, 10, 10), client.getPassportId().getIssueDate());
        assertEquals("MVD Russia", client.getPassportId().getIssueBranch());
        assertNotNull(client.getEmploymentId());
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("Если клиент не найден, то выбрасывается исключение")
    void updateClientInformationFromScoringDataShouldThrowWhenClientNotFound() {
        ScoringDataEntity scoring = ScoringDataEntity.builder()
                .passportSeries("0000")
                .passportNumber("000000")
                .build();
        when(clientRepository.findByPassportId_SeriesAndPassportId_Number("0000", "000000"))
                .thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () ->
                clientManagerService.updateClientInformationFromScoringData(scoring)
        );
        verify(clientRepository, never()).save(any());
    }
}