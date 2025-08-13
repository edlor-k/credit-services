package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.exception.ClientNotFoundException;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.service.impl.ClientLookupServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientLookupServiceImplTest {

    @Mock
    private StatementManagerService statementManagerService;

    @InjectMocks
    private ClientLookupServiceImpl clientLookupService;

    @Test
    @DisplayName("Возвращает email, если клиент найден у заявки")
    void getEmailByStatementId_returnsEmail() {
        UUID statementId = UUID.randomUUID();
        StatementEntity statement = mock(StatementEntity.class);
        ClientEntity client = mock(ClientEntity.class);

        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statement);
        when(statement.getClient()).thenReturn(client);
        when(client.getEmail()).thenReturn("user@example.com");

        String email = clientLookupService.getEmailByStatementId(statementId);

        assertEquals("user@example.com", email);
        verify(statementManagerService, times(1)).getStatementOrThrow(statementId);
        verify(statement, times(1)).getClient();
        verify(client, times(1)).getEmail();
    }

    @Test
    @DisplayName("Бросает ClientNotFoundException, если у заявки нет клиента")
    void getEmailByStatementId_throwsWhenClientMissing() {
        UUID statementId = UUID.randomUUID();
        StatementEntity statement = mock(StatementEntity.class);

        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statement);
        when(statement.getClient()).thenReturn(null);

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> clientLookupService.getEmailByStatementId(statementId)
        );
        assertTrue(ex.getMessage().contains(statementId.toString()));
        verify(statementManagerService).getStatementOrThrow(statementId);
        verify(statement).getClient();
        verifyNoMoreInteractions(statementManagerService, statement);
    }

    @Test
    @DisplayName("Пробрасывает исключение, если заявка не найдена (исключение из getStatementOrThrow)")
    void getEmailByStatementId_propagatesWhenStatementMissing() {
        UUID statementId = UUID.randomUUID();
        RuntimeException notFound = new RuntimeException("Statement not found");

        when(statementManagerService.getStatementOrThrow(statementId)).thenThrow(notFound);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> clientLookupService.getEmailByStatementId(statementId)
        );
        assertSame(notFound, ex);
        verify(statementManagerService).getStatementOrThrow(statementId);
        verifyNoMoreInteractions(statementManagerService);
    }
}
