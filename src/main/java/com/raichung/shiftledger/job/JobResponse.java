package com.raichung.shiftledger.job;

import java.math.BigDecimal;

public record JobResponse(Long id, String employerName, BigDecimal weekdayRate,
                          BigDecimal saturdayRate, BigDecimal sundayRate, BigDecimal publicHolidayRate) {

    static JobResponse from(Job job) {
        return new JobResponse(job.getId(), job.getEmployerName(), job.getWeekdayRate(),
                job.getSaturdayRate(), job.getSundayRate(), job.getPublicHolidayRate());
    }
}
