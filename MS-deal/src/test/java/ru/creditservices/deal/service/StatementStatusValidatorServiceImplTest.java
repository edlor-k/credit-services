package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.creditservices.deal.exception.InvalidApplicationStatus;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.service.impl.StatementStatusValidatorServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StatementStatusValidatorServiceImplTest {

    private final StatementStatusValidatorServiceImpl validator = new StatementStatusValidatorServiceImpl();

    @Test
    @DisplayName("Не бросает исключение, если текущий статус входит в список разрешённых")
    void validateStatus_ok_whenAllowed() {
        StatementEntity statement = StatementEntity.builder()
                .statementId(UUID.randomUUID())
                .status(ApplicationStatus.PREAPPROVAL)
                .build();

        assertDoesNotThrow(() ->
                validator.validateStatus(statement,
                        List.of(ApplicationStatus.PREAPPROVAL, ApplicationStatus.CC_APPROVED),
                        "perform some action")
        );
    }

    @Test
    @DisplayName("Бросает InvalidApplicationStatus, если текущий статус НЕ входит в список")
    void validateStatus_throws_whenNotAllowed() {
        UUID id = UUID.randomUUID();
        StatementEntity statement = StatementEntity.builder()
                .statementId(id)
                .status(ApplicationStatus.CC_DENIED)
                .build();

        InvalidApplicationStatus ex = assertThrows(
                InvalidApplicationStatus.class,
                () -> validator.validateStatus(statement,
                        List.of(ApplicationStatus.PREAPPROVAL, ApplicationStatus.CC_APPROVED),
                        "perform some action")
        );

        assertTrue(ex.getMessage().contains(id.toString()));
        assertTrue(ex.getMessage().contains("current status is CC_DENIED"));
    }

    @ParameterizedTest(name = "Разрешённый статус: {0}")
    @EnumSource(value = ApplicationStatus.class, names = {"PREAPPROVAL", "CC_APPROVED"}, mode = EnumSource.Mode.INCLUDE)
    @DisplayName("Параметризованный: для разрешённых статусов исключение не кидается")
    void validateStatus_ok_forEachAllowedStatus(ApplicationStatus allowed) {
        StatementEntity statement = StatementEntity.builder()
                .statementId(UUID.randomUUID())
                .status(allowed)
                .build();

        assertDoesNotThrow(() ->
                validator.validateStatus(statement,
                        List.of(ApplicationStatus.PREAPPROVAL, ApplicationStatus.CC_APPROVED),
                        "do something")
        );
    }
}
