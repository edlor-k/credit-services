package ru.creditservices.calculator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {
    TRAINEE("Стажер"),
    JUNIOR("Младший специалист"),
    MIDDLE("Специалист"),
    SENIOR("Старший специалист"),
    LEAD("Ведущий специалист");

    public final String positionDescription;
}
