package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.model.entity.LoanStatementEntity;

@Mapper(componentModel = "spring")
public interface LoanStatementMapper {
    LoanStatementRequestDto toDto(LoanStatementEntity entity);
    LoanStatementEntity toEntity(LoanStatementRequestDto dto);
}
