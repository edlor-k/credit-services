package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.EmploymentDto;
import ru.creditservices.deal.model.entity.EmploymentEntity;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {
    EmploymentDto toDto(EmploymentEntity entity);
    EmploymentEntity toEntity(EmploymentDto dto);
}
