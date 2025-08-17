package ru.creditservices.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.statement.model.enums.ErrorCode;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private ErrorCode code;
    private String message;
    private Map<String, String> details;
}
