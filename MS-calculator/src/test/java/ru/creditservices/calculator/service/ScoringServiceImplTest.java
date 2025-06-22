package ru.creditservices.calculator.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.exception.ScoringException;
import ru.creditservices.calculator.mapper.CreditMapper;
import ru.creditservices.calculator.mapper.ScoringDataMapper;
import ru.creditservices.calculator.model.entity.CreditEntity;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.service.impl.ScoringServiceImpl;
import ru.creditservices.calculator.service.scoring.ScoringCalculator;
import ru.creditservices.calculator.service.scoring.ScoringValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoringServiceImplTest {

    @InjectMocks
    private ScoringServiceImpl scoringService;

    @Mock
    private ScoringDataMapper scoringDataMapper;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private ScoringCalculator scoringCalculator;

    @Mock
    private ScoringValidator scoringValidator;

    @Test
    @DisplayName("Тестирование получения финальной информации по кредиту с корректными данными")
    void testGetFinalCreditInfoWhenDataIsValid() {
        ScoringDataDto scoringRequest = mock(ScoringDataDto.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        when(scoringDataMapper.toEntity(scoringRequest)).thenReturn(scoringData);

        int term = 12;
        BigDecimal amount = new BigDecimal("100000");
        BigDecimal finalRate = new BigDecimal("13.5");
        BigDecimal totalAmount = new BigDecimal("105000");
        BigDecimal monthlyPayment = new BigDecimal("9500");
        BigDecimal psk = new BigDecimal("14.2");
        LocalDate now = LocalDate.now();

        when(scoringData.getTerm()).thenReturn(term);
        when(scoringData.getAmount()).thenReturn(amount);

        when(scoringCalculator.calculateFinalRate(scoringData)).thenReturn(finalRate);
        when(scoringCalculator.calculateTotalAmount(scoringData)).thenReturn(totalAmount);

        List<PaymentScheduleElementEntity> schedule = List.of(
                PaymentScheduleElementEntity.builder()
                        .number(1)
                        .date(now.plusMonths(1))
                        .totalPayment(monthlyPayment)
                        .build()
        );

        when(scoringCalculator.calculateSchedule(
                eq(totalAmount),
                eq(term),
                eq(finalRate),
                any(LocalDate.class),
                any(BigDecimal.class)
        )).thenReturn(schedule);

        when(scoringCalculator.calculatePsk(eq(schedule), eq(amount), any(LocalDate.class)))
                .thenReturn(psk);

        CreditDto expectedDto = mock(CreditDto.class);
        when(creditMapper.toDto(any(CreditEntity.class))).thenReturn(expectedDto);

        CreditDto result = scoringService.getFinalCreditInfo(scoringRequest);

        assertSame(expectedDto, result);

        verify(scoringDataMapper).toEntity(scoringRequest);
        verify(scoringValidator).validate(scoringData);
        verify(scoringCalculator).calculateFinalRate(scoringData);
        verify(scoringCalculator).calculateTotalAmount(scoringData);
        verify(scoringCalculator).calculateSchedule(
                eq(totalAmount),
                eq(term),
                eq(finalRate),
                any(LocalDate.class),
                any(BigDecimal.class)
        );        verify(scoringCalculator).calculatePsk(eq(schedule), eq(amount), any(LocalDate.class));
        verify(creditMapper).toDto(any(CreditEntity.class));
    }

    @Test
    @DisplayName("Должно бросать ScoringException, если валидация не проходит")
    void testGetFinalCreditInfoWhenValidationFails() {
        ScoringDataDto scoringRequest = mock(ScoringDataDto.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        when(scoringDataMapper.toEntity(scoringRequest)).thenReturn(scoringData);

        doThrow(new ScoringException("validation failed")).when(scoringValidator).validate(scoringData);

        Assertions.assertThrows(
                ScoringException.class,
                () -> scoringService.getFinalCreditInfo(scoringRequest)
        );

        verify(scoringDataMapper).toEntity(scoringRequest);
        verify(scoringValidator).validate(scoringData);
        verifyNoMoreInteractions(scoringCalculator, creditMapper);
    }
}
