package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.EmploymentDto;
import ru.creditservices.calculator.model.entity.EmploymentEntity;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {
    EmploymentDto toDto(EmploymentEntity entity);
    EmploymentEntity toEntity(EmploymentDto dto);
}
