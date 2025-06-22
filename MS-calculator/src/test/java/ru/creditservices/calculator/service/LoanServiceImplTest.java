package ru.creditservices.calculator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.mapper.LoanOfferMapper;
import ru.creditservices.calculator.mapper.LoanStatementMapper;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;
import ru.creditservices.calculator.service.impl.LoanServiceImpl;
import ru.creditservices.calculator.service.prescoring.LoanOfferCalculator;
import ru.creditservices.calculator.service.prescoring.LoanPrescoringValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {
    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private LoanPrescoringValidator prescoringValidator;

    @Mock
    private LoanOfferCalculator loanOfferCalculator;

    @Mock
    private LoanStatementMapper statementMapper;

    @Mock
    private LoanOfferMapper offerMapper;

    @Test
    void testGetLoanOffersReturnsSortedLoanOffersWhenRequestIsValid() {
        var request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("500000"))
                .term(24)
                .firstName("Petr")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@example.com")
                .birthdate(LocalDate.of(1985, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        var statementEntity = mock(LoanStatementEntity.class);
        when(statementMapper.toEntity(request)).thenReturn(statementEntity);

        List<LoanOfferDto> expectedDtos = List.of(
                LoanOfferDto.builder().rate(new BigDecimal("7.5")).build(),
                LoanOfferDto.builder().rate(new BigDecimal("8.0")).build(),
                LoanOfferDto.builder().rate(new BigDecimal("8.5")).build(),
                LoanOfferDto.builder().rate(new BigDecimal("9.0")).build()
        );

        when(loanOfferCalculator.calculateRate(anyBoolean(), anyBoolean()))
                .thenReturn(new BigDecimal("9.0"))
                .thenReturn(new BigDecimal("8.5"))
                .thenReturn(new BigDecimal("8.0"))
                .thenReturn(new BigDecimal("7.5"));
        when(loanOfferCalculator.calculateTotalAmount(any(), anyBoolean())).thenReturn(new BigDecimal("500000"));
        when(loanOfferCalculator.getMonthlyPayment(any(), anyInt(), any())).thenReturn(new BigDecimal("22916.67"));

        when(offerMapper.toDto(anyList())).thenReturn(expectedDtos);

        List<LoanOfferDto> actualDtos = loanService.getLoanOffers(request);

        assertEquals(expectedDtos, actualDtos);

        verify(statementMapper).toEntity(request);
        verify(prescoringValidator).validate(statementEntity);
        verify(offerMapper).toDto(anyList());
    }

    @Test
    void testGetLoanOffersShouldThrowLoanPrescoringExceptionWhenValidationFails() {
        var request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("1000"))
                .term(2)
                .firstName("A")
                .lastName("B")
                .email("wrong")
                .birthdate(LocalDate.now())
                .passportSeries("wrong")
                .passportNumber("wrong")
                .build();

        var statementEntity = mock(LoanStatementEntity.class);
        when(statementMapper.toEntity(request)).thenReturn(statementEntity);

        doThrow(new LoanPrescoringException("Validation failed")).when(prescoringValidator).validate(statementEntity);

        LoanPrescoringException ex = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertEquals("Validation failed", ex.getMessage());

        verify(statementMapper).toEntity(request);
        verify(prescoringValidator).validate(statementEntity);
        verifyNoInteractions(offerMapper);
    }

}
