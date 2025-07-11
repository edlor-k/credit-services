package ru.creditservices.deal.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.model.entity.LoanOfferEntity;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOfferDto toDto(LoanOfferEntity entity);
    LoanOfferEntity toEntity(LoanOfferDto dto);
}
