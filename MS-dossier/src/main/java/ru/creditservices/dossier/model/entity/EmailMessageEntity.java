package ru.creditservices.dossier.model.entity;

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
public class EmailMessageEntity {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}
