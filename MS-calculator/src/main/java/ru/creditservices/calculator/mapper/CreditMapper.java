package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.model.entity.CreditEntity;

@Mapper(componentModel = "spring", uses = PaymentScheduleMapper.class)
public interface CreditMapper {
    CreditDto toDto(CreditEntity entity);
    CreditEntity toEntity(CreditDto dto);
}
