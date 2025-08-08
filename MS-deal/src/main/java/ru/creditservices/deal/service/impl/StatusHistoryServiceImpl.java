package ru.creditservices.deal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.model.jsonb.StatusHistoryElement;
import ru.creditservices.deal.service.StatusHistoryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StatusHistoryServiceImpl implements StatusHistoryService {
    @Override
    public List<StatusHistoryElement> addStatus(List<StatusHistoryElement> history, ApplicationStatus status) {
        List<StatusHistoryElement> result = new ArrayList<>(history);
        result.add(StatusHistoryElement.builder()
                .status(status)
                .time(LocalDateTime.now())
                .build());
        return result;
    }

    @Override
    public List<StatusHistoryElement> initialHistory() {
        return List.of(StatusHistoryElement.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .build());
    }
}
