package ru.creditservices.calculator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentStatus {
    EMPLOYED("Работающий"),
    UNEMPLOYED("Безработный"),
    SELF_EMPLOYED("Самозанятый"),
    BUSINESS_OWNER("Владелец бизнеса"),
    RETIRED("Пенсионер"),
    STUDENT("Студент");

    public final String employmentStatusDescription;
}
