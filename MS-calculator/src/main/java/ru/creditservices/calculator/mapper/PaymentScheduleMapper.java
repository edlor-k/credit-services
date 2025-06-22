package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.PaymentScheduleElementDto;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;

@Mapper(componentModel = "spring")
public interface PaymentScheduleMapper {
    PaymentScheduleElementEntity toEntity(PaymentScheduleElementDto dto);
    PaymentScheduleElementDto toDto(PaymentScheduleElementEntity entity);
}
