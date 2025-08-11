package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.model.entity.StatementEntity;

@Mapper(componentModel = "spring")
public interface StatementMapper {
    StatementEntity toEntity(StatementDto dto);
    StatementDto toDto(StatementEntity entity);
}
