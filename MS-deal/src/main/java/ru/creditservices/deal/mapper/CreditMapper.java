package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.model.entity.CreditEntity;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    CreditDto toDto(CreditEntity creditEntity);
    CreditEntity toEntity(CreditDto creditDto);
}
