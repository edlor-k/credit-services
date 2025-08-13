package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.model.jsonb.StatusHistoryElement;
import ru.creditservices.deal.service.impl.StatusHistoryServiceImpl;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatusHistoryServiceImplTest {

    @Test
    @DisplayName("Статус должен корректно добавляться в историю")
    void addStatusShouldAddStatusToHistory() {
        StatusHistoryElement element = StatusHistoryElement.builder()
                .status(ApplicationStatus.CC_APPROVED)
                .time(LocalDateTime.of(2023-12-12, Month.APRIL, 10, 10, 0))
                .build();
        StatusHistoryServiceImpl service = new StatusHistoryServiceImpl();
        var initialHistory = service.initialHistory();
        var updatedHistory = service.addStatus(initialHistory, element.getStatus());
        assertFalse(updatedHistory.isEmpty(), "История не должна быть пустой");
        assertEquals(2, updatedHistory.size(), "История должна содержать два элемента");
        assertEquals(ApplicationStatus.CC_APPROVED, updatedHistory.getLast().getStatus(),
                "Последний статус должен быть CC_APPROVED");
    }

    @Test
    @DisplayName("Начальная история должна содержать статус PREAPPROVAL")
    void initialHistoryShouldContainPreapprovalStatus() {
        StatusHistoryServiceImpl service = new StatusHistoryServiceImpl();
        var history = service.initialHistory();

        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(1, history.size(), "История должна содержать один элемент");
        assertEquals(ApplicationStatus.PREAPPROVAL, history.getFirst().getStatus(),
                "Первый статус должен быть PREAPPROVAL");
    }
}
