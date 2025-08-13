package ru.creditservices.deal.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.service.DealService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private DealService dealService;

    @InjectMocks
    private AdminController adminController;

    @Test
    @DisplayName("GET /deal/admin/statement — возвращает список заявок")
    void getAllStatements_returnsListFromService() {
        StatementDto s1 = mock(StatementDto.class);
        StatementDto s2 = mock(StatementDto.class);
        List<StatementDto> expected = List.of(s1, s2);

        when(dealService.getAllStatements()).thenReturn(expected);

        ResponseEntity<List<StatementDto>> response = adminController.getAllStatements();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(s1, s2);
        verify(dealService, times(1)).getAllStatements();
    }

    @Test
    @DisplayName("GET /deal/admin/statement/{id} — возвращает заявку по id")
    void getStatementById_returnsDtoFromService() {
        UUID id = UUID.randomUUID();
        StatementDto dto = mock(StatementDto.class);

        when(dealService.getStatementById(id)).thenReturn(dto);

        ResponseEntity<StatementDto> response = adminController.getStatementById(id);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(dto);
        verify(dealService, times(1)).getStatementById(id);
    }

    @Test
    @DisplayName("PUT /deal/admin/statement/status — делегирует обновление статуса")
    void updateStatementStatus_delegatesAndReturnsOk() {
        UUID id = UUID.randomUUID();
        String status = "DOCUMENT_CREATED";

        doNothing().when(dealService).updateStatementStatus(id, status);

        ResponseEntity<Void> response = adminController.updateStatementStatus(id, status);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(dealService, times(1)).updateStatementStatus(id, status);
    }
}
