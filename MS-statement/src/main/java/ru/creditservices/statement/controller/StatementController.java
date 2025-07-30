package ru.creditservices.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;
import ru.creditservices.statement.service.GetLoanOffersService;
import ru.creditservices.statement.service.SelectLoanOfferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statement")
@Tag(name = "Statement API", description = "API for managing statements")
@Slf4j
public class StatementController implements StatementApi {

    private final GetLoanOffersService getLoanOffersService;
    private final SelectLoanOfferService selectLoanOfferService;

    @Override
    @PostMapping
    @Operation(
            summary = "Получение предложений по кредиту",
            description = "Формирует список возможных кредитных предложений на основе анкеты клиента",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение предложений"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных анкеты",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "422", description = "Ошибка бизнес-валидации",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@Valid @RequestBody LoanStatementRequestDto request) {
        log.info("Request for getting loan offers: {}", request);
        List<LoanOfferDto> loanOffers = getLoanOffersService.getLoanOffers(request);
        log.debug("Loan offers: {}", loanOffers);
        return ResponseEntity.ok(loanOffers);
    }

    @Override
    @PostMapping("/offer")
    @Operation(
            summary = "Выбор кредитного предложения",
            description = "Клиент выбирает одно из предложенных условий кредита",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации предложения",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    public ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        log.info("Request for selecting loan offer: {}", loanOfferDto);
        selectLoanOfferService.selectLoanOffer(loanOfferDto);
        log.debug("Loan offer selected");
        return ResponseEntity.ok().build();
    }
}
