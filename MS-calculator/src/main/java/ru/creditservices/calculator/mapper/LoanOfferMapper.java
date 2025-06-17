package ru.creditservices.calculator.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.model.entity.LoanOfferEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOfferDto toDto(LoanOfferDto entity);
    LoanOfferEntity toEntity(LoanOfferDto dto);

    List<LoanOfferDto> toDto(List<LoanOfferEntity> entityList);
}
