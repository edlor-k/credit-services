package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;

@Mapper(componentModel = "spring", uses = EmploymentMapper.class)
public interface ScoringDataMapper {
    ScoringDataDto toDto(ScoringDataEntity entity);
    ScoringDataEntity toEntity(ScoringDataDto dto);
}
