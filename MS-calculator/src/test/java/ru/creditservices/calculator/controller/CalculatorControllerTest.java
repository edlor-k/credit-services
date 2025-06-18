package ru.creditservices.calculator.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.creditservices.calculator.dto.*;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;
import ru.creditservices.calculator.service.LoanService;
import ru.creditservices.calculator.service.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalculatorControllerTest {

    private AutoCloseable mocks;

    @Mock
    private LoanService loanService;

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private CalculatorController controller;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getLoanOffers_returnsCorrectResponse() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        LoanOfferDto offer1 = LoanOfferDto.builder().requestedAmount(BigDecimal.valueOf(50000)).build();
        LoanOfferDto offer2 = LoanOfferDto.builder().requestedAmount(BigDecimal.valueOf(50000)).build();

        when(loanService.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(List.of(offer1, offer2));

        ResponseEntity<List<LoanOfferDto>> response = controller.getLoanOffers(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(loanService).getLoanOffers(request);
    }

    @Test
    void calculateCredit_returnsCorrectResponse() {
        ScoringDataDto scoringRequest = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthdate(LocalDate.parse("1990-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .passportIssueDate(LocalDate.parse("2010-01-01"))
                .passportIssueBranch("ОВД")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employmentINN("1234567890")
                        .salary(BigDecimal.valueOf(20000))
                        .workExperienceTotal(36)
                        .workExperienceCurrent(12)
                        .build())
                .accountNumber("12345678901234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        CreditDto credit = CreditDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .monthlyPayment(BigDecimal.valueOf(4000))
                .rate(BigDecimal.valueOf(10))
                .term(12)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        when(scoringService.getFinalCreditInfo(any(ScoringDataDto.class))).thenReturn(credit);

        ResponseEntity<CreditDto> response = controller.calculateCredit(scoringRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(credit, response.getBody());
        verify(scoringService).getFinalCreditInfo(scoringRequest);
    }
}
