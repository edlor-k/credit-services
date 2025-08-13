package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.mapper.StatementMapper;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.service.AdminService;
import ru.creditservices.deal.service.StatementManagerService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final StatementMapper statementMapper;
    private final StatementManagerService statementManagerService;

    @Override
    public List<StatementDto> getAllStatements() {
        log.info("Получение всех анкет из StatementManagerService");
        List<StatementEntity> entities = statementManagerService.getAllStatements();
        List<StatementDto> statements = entities.stream()
                .map(statementMapper::toDto)
                .toList();
        log.debug("Преобразовано {} анкет в DTO", statements.size());
        return statements;
    }

    @Override
    public StatementDto getStatementById(UUID statementId) {
        log.info("Запрос анкеты по statementId={}", statementId);
        StatementEntity entity = statementManagerService.getStatementOrThrow(statementId);
        StatementDto dto = statementMapper.toDto(entity);
        log.debug("Анкета успешно найдена и преобразована: {}", dto);
        return dto;
    }
}
