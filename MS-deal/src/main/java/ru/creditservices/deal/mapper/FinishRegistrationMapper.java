package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.model.entity.FinishRegistrationEntity;

@Mapper(componentModel = "spring", uses = EmploymentMapper.class)
public interface FinishRegistrationMapper {
    FinishRegistrationRequestDto toDto(FinishRegistrationEntity entity);
    FinishRegistrationEntity toEntity(FinishRegistrationRequestDto dto);
}
