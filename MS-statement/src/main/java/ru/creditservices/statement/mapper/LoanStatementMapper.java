package ru.creditservices.statement.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.model.entity.LoanStatementEntity;

@Mapper(componentModel = "spring")
public interface LoanStatementMapper {
    LoanStatementEntity toEntity(LoanStatementRequestDto dto);
}
