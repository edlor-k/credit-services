package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;

@Mapper(componentModel = "spring")
public interface LoanStatementMapper {
    LoanStatementEntity toEntity(LoanStatementRequestDto dto);
    LoanStatementRequestDto toDto(LoanStatementEntity entity);
}
