package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.common.AppTime;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ShiftResponse(Long id, Long jobId, String employerName,
                            OffsetDateTime startTime, OffsetDateTime endTime,
                            int breakMinutes, String note, BigDecimal pay) {

    static ShiftResponse from(Shift s, BigDecimal pay) {
        return new ShiftResponse(s.getId(), s.getJob().getId(), s.getJob().getEmployerName(),
                s.getStartTime().atZone(AppTime.ZONE).toOffsetDateTime(),
                s.getEndTime().atZone(AppTime.ZONE).toOffsetDateTime(),
                s.getBreakMinutes(), s.getNote(), pay);
    }
}
