package ru.creditservices.calculator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("Мужской"),
    FEMALE("Женский"),
    OTHER("Другой");

    public final String genderDescription;
}
