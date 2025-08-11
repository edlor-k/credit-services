package ru.creditservices.dossier.mapper;

import org.mapstruct.Mapper;
import ru.creditservices.dossier.dto.EmailMessageDto;
import ru.creditservices.dossier.model.entity.EmailMessageEntity;

@Mapper(componentModel = "spring")
public interface EmailMessageMapper {
    EmailMessageEntity toEntity(EmailMessageDto messageDto);
}
