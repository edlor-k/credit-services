package ru.creditservices.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.dossier.model.enums.EmailTheme;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для Email сообщений")
public class EmailMessageDto {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}
