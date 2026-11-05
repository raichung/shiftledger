package com.raichung.shiftledger.shift;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record CreateShiftRequest(
        @NotNull Long jobId,
        @NotNull OffsetDateTime startTime,
        @NotNull OffsetDateTime endTime,
        @PositiveOrZero Integer breakMinutes,
        @Size(max = 255) String note
) {}
