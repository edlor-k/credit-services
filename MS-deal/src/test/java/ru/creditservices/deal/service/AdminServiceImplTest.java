package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.mapper.StatementMapper;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.service.impl.AdminServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock private StatementMapper statementMapper;
    @Mock private StatementManagerService statementManagerService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("getAllStatements: маппит список сущностей в список DTO, сохраняя порядок")
    void getAllStatements_mapsEntitiesToDtos() {
        StatementEntity e1 = mock(StatementEntity.class);
        StatementEntity e2 = mock(StatementEntity.class);
        StatementDto d1 = new StatementDto();
        StatementDto d2 = new StatementDto();

        when(statementManagerService.getAllStatements()).thenReturn(List.of(e1, e2));
        when(statementMapper.toDto(e1)).thenReturn(d1);
        when(statementMapper.toDto(e2)).thenReturn(d2);

        List<StatementDto> result = adminService.getAllStatements();

        assertThat(result).containsExactly(d1, d2);

        InOrder inOrder = inOrder(statementManagerService, statementMapper);
        inOrder.verify(statementManagerService).getAllStatements();
        inOrder.verify(statementMapper).toDto(e1);
        inOrder.verify(statementMapper).toDto(e2);
        verifyNoMoreInteractions(statementManagerService, statementMapper);
    }

    @Test
    @DisplayName("getAllStatements: когда список пуст — возвращает пустой список и не вызывает mapper")
    void getAllStatements_emptyList_noMapping() {
        when(statementManagerService.getAllStatements()).thenReturn(List.of());

        List<StatementDto> result = adminService.getAllStatements();

        assertThat(result).isEmpty();
        verify(statementManagerService).getAllStatements();
        verifyNoInteractions(statementMapper);
    }

    @Test
    @DisplayName("getStatementById: возвращает DTO, если сущность найдена")
    void getStatementById_returnsDto() {
        UUID id = UUID.randomUUID();
        StatementEntity entity = mock(StatementEntity.class);
        StatementDto dto = new StatementDto();

        when(statementManagerService.getStatementOrThrow(id)).thenReturn(entity);
        when(statementMapper.toDto(entity)).thenReturn(dto);

        StatementDto result = adminService.getStatementById(id);

        assertSame(dto, result);
        verify(statementManagerService).getStatementOrThrow(id);
        verify(statementMapper).toDto(entity);
        verifyNoMoreInteractions(statementManagerService, statementMapper);
    }

    @Test
    @DisplayName("getStatementById: пробрасывает исключение, если заявка не найдена")
    void getStatementById_propagatesException() {
        UUID id = UUID.randomUUID();
        RuntimeException notFound = new RuntimeException("Statement not found");

        when(statementManagerService.getStatementOrThrow(id)).thenThrow(notFound);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> adminService.getStatementById(id));
        assertSame(notFound, ex);

        verify(statementManagerService).getStatementOrThrow(id);
        verifyNoInteractions(statementMapper);
    }
}
