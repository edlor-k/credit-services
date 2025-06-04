package ru.creditservices.calculator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaritalStatus {
    MARRIED("Женат/Замужем"),
    SINGLE("Холост/Не замужем"),
    DIVORCED("Разведен/Разведена"),
    WIDOWED("Вдовец/Вдова");

    public final String maritalStatusDescription;
}
