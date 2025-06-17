package ru.creditservices.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.creditservices.calculator.dto.*;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;
import ru.creditservices.calculator.service.impl.LoanService;
import ru.creditservices.calculator.service.impl.ScoringService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private ScoringService scoringService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 4 loan offers when valid request is sent")
    void returnsLoanOffersForValidRequest() throws Exception {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(24)
                .firstName("Петров")
                .lastName("Петр")
                .middleName("Петрович")
                .email("petrpert@mail.ru")
                .passportSeries("2222")
                .passportNumber("222222")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        UUID statementId = UUID.randomUUID();
        BigDecimal baseAmount = request.getAmount();
        int term = request.getTerm();
        BigDecimal baseRate = BigDecimal.valueOf(13.0);
        BigDecimal insuranceRate = BigDecimal.valueOf(2.0);
        BigDecimal insuranceCost = BigDecimal.valueOf(5_000);
        BigDecimal salaryDiscount = BigDecimal.valueOf(1.0);

        List<LoanOfferDto> offers = new ArrayList<>();

        for (boolean isInsuranceEnabled : new boolean[]{true, false}) {
            for (boolean isSalaryClient : new boolean[]{true, false}) {
                BigDecimal rate = baseRate;
                BigDecimal totalAmount = baseAmount;

                if (isInsuranceEnabled) {
                    rate = rate.subtract(insuranceRate);
                    totalAmount = totalAmount.add(insuranceCost);
                }

                if (isSalaryClient) {
                    rate = rate.subtract(salaryDiscount);
                }

                BigDecimal monthlyPayment = calculateMonthlyPayment(rate, totalAmount, term);

                offers.add(LoanOfferDto.builder()
                        .statementId(statementId)
                        .requestedAmount(baseAmount)
                        .totalAmount(totalAmount)
                        .term(term)
                        .monthlyPayment(monthlyPayment)
                        .rate(rate)
                        .isInsuranceEnabled(isInsuranceEnabled)
                        .isSalaryClient(isSalaryClient)
                        .build());
            }
        }

        offers.sort(Comparator.comparing(LoanOfferDto::getRate));
        when(loanService.getLoanOffers(Mockito.eq(request))).thenReturn(offers);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].statementId").value(statementId.toString()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid scoring data is sent")
    void returnsBadRequestForInvalidScoringData() throws Exception {
        ScoringDataDto invalidRequest = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(10_000))
                .term(3)
                .firstName("")
                .lastName("")
                .gender(null)
                .birthdate(LocalDate.now())
                .passportSeries("12A")
                .passportNumber("12345")
                .passportIssueDate(LocalDate.now().plusDays(1))
                .passportIssueBranch("УФ")
                .maritalStatus(null)
                .dependentAmount(-1)
                .employment(EmploymentDto.builder()
                        .employmentStatus(null)
                        .employmentINN(null)
                        .salary(null)
                        .position(null)
                        .workExperienceTotal(10)
                        .workExperienceCurrent(1)
                        .build())
                .accountNumber("123")
                .isInsuranceEnabled(null)
                .isSalaryClient(null)
                .build();

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations.length()")
                        .value(org.hamcrest.Matchers.greaterThan(5)));
    }

    @DisplayName("Should return credit data when valid scoring data is sent")
    @Test
    void returnsCreditDataForValidRequest() throws Exception {
        ScoringDataDto request = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(24)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2010, 1, 1))
                .passportIssueBranch("УФМС России")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employmentINN("1234567890")
                        .salary(BigDecimal.valueOf(80_000))
                        .position(ru.creditservices.calculator.model.enums.Position.JUNIOR)
                        .workExperienceTotal(24)
                        .workExperienceCurrent(12)
                        .build())
                .accountNumber("12345678901234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        CreditDto response = CreditDto.builder()
                .amount(BigDecimal.valueOf(505_000))
                .term(24)
                .monthlyPayment(BigDecimal.valueOf(22916.67))
                .rate(BigDecimal.valueOf(11.0))
                .psk(BigDecimal.valueOf(13.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .paymentSchedule(Collections.emptyList())
                .build();

        when(scoringService.getFinalCreditInfo(Mockito.eq(request))).thenReturn(response);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(505000))
                .andExpect(jsonPath("$.term").value(24))
                .andExpect(jsonPath("$.monthlyPayment").value(22916.67))
                .andExpect(jsonPath("$.rate").value(11.0))
                .andExpect(jsonPath("$.psk").value(13.5));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid loan statement is sent")
    void returnsBadRequestForInvalidLoanStatement() throws Exception {
        LoanStatementRequestDto invalidRequest = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(10_000))
                .term(3)
                .firstName("")
                .lastName("")
                .middleName("А")
                .email("not-an-email")
                .birthdate(LocalDate.now())
                .passportSeries("12A4")
                .passportNumber("12345")
                .build();

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations.length()")
                        .value(org.hamcrest.Matchers.greaterThan(5)));
    }


    private static BigDecimal calculateMonthlyPayment(BigDecimal rate, BigDecimal totalAmount, int term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal numerator = totalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(monthlyRate).pow(-term, MathContext.DECIMAL128)
        );
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
