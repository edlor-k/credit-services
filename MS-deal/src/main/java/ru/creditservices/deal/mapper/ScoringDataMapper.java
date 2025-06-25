package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.ScoringDataDto;
import ru.creditservices.deal.model.entity.ScoringDataEntity;

@Mapper(componentModel = "spring")
public interface ScoringDataMapper {
    ScoringDataEntity toEntity(ScoringDataDto dto);
    ScoringDataDto toDto(ScoringDataEntity entity);
}
