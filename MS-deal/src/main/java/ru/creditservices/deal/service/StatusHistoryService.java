package ru.creditservices.deal.service;

import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.model.jsonb.StatusHistoryElement;

import java.util.List;

public interface StatusHistoryService {
    List<StatusHistoryElement> addStatus(List<StatusHistoryElement> history, ApplicationStatus status);
    List<StatusHistoryElement> initialHistory();
}
