package ru.creditservices.statement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.mapper.LoanStatementMapper;
import ru.creditservices.statement.model.entity.LoanStatementEntity;
import ru.creditservices.statement.service.DealClientService;
import ru.creditservices.statement.service.PrescoringService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLoanOfferServiceImplTest {

    @Mock
    private PrescoringService prescoringService;
    @Mock
    private LoanStatementMapper loanStatementMapper;
    @Mock
    private DealClientService dealClientService;

    @InjectMocks
    private GetLoanOfferServiceImpl getLoanOfferService;

    @Test
    void getLoanOffersSuccess() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanStatementEntity entity = new LoanStatementEntity();
        List<LoanOfferDto> offerList = Arrays.asList(new LoanOfferDto(), new LoanOfferDto());

        when(loanStatementMapper.toEntity(requestDto)).thenReturn(entity);
        doNothing().when(prescoringService).businessValidate(entity);
        when(dealClientService.fetchLoanOffers(requestDto)).thenReturn(offerList);

        List<LoanOfferDto> result = getLoanOfferService.getLoanOffers(requestDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loanStatementMapper).toEntity(requestDto);
        verify(prescoringService).businessValidate(entity);
        verify(dealClientService).fetchLoanOffers(requestDto);
    }

    @Test
    void getLoanOffersPrescoringThrowsException() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanStatementEntity entity = new LoanStatementEntity();

        when(loanStatementMapper.toEntity(requestDto)).thenReturn(entity);
        doThrow(new RuntimeException("Prescoring failed")).when(prescoringService).businessValidate(entity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> getLoanOfferService.getLoanOffers(requestDto));
        assertEquals("Prescoring failed", exception.getMessage());

        verify(loanStatementMapper).toEntity(requestDto);
        verify(prescoringService).businessValidate(entity);
        verifyNoInteractions(dealClientService);
    }

    @Test
    void getLoanOffersDealClientThrowsException() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanStatementEntity entity = new LoanStatementEntity();

        when(loanStatementMapper.toEntity(requestDto)).thenReturn(entity);
        doNothing().when(prescoringService).businessValidate(entity);
        when(dealClientService.fetchLoanOffers(requestDto)).thenThrow(new RuntimeException("Deal client unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> getLoanOfferService.getLoanOffers(requestDto));
        assertEquals("Deal client unavailable", exception.getMessage());

        verify(loanStatementMapper).toEntity(requestDto);
        verify(prescoringService).businessValidate(entity);
        verify(dealClientService).fetchLoanOffers(requestDto);
    }
}
