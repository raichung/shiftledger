package com.raichung.shiftledger.job;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateJobRequest(
        @NotBlank @Size(max = 150) String employerName,
        @NotNull @Positive @Digits(integer = 6, fraction = 2) BigDecimal weekdayRate,
        @NotNull @Positive @Digits(integer = 6, fraction = 2) BigDecimal saturdayRate,
        @NotNull @Positive @Digits(integer = 6, fraction = 2) BigDecimal sundayRate,
        @NotNull @Positive @Digits(integer = 6, fraction = 2) BigDecimal publicHolidayRate
) {}
