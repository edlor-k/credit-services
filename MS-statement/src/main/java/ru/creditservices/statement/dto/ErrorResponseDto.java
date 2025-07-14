package ru.creditservices.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String code;
    private String message;
    private Map<String, String> details;
}
