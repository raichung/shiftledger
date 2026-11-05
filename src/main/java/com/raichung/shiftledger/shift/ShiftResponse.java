package com.raichung.shiftledger.shift;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public record ShiftResponse(Long id, Long jobId, String employerName,
                            OffsetDateTime startTime, OffsetDateTime endTime,
                            int breakMinutes, String note) {

    private static final ZoneId ADELAIDE = ZoneId.of("Australia/Adelaide");

    static ShiftResponse from(Shift s) {
        return new ShiftResponse(s.getId(), s.getJob().getId(), s.getJob().getEmployerName(),
                s.getStartTime().atZone(ADELAIDE).toOffsetDateTime(),
                s.getEndTime().atZone(ADELAIDE).toOffsetDateTime(),
                s.getBreakMinutes(), s.getNote());
    }
}
