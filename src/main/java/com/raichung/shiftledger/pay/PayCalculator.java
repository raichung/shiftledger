package com.raichung.shiftledger.pay;

import com.raichung.shiftledger.job.Job;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.Set;

@Component
public class PayCalculator {

    public BigDecimal calculate(Job job, Instant start, Instant end, int breakMinutes,
                                ZoneId zone, Set<LocalDate> holidays) {
        long totalMinutes = Duration.between(start, end).toMinutes();
        long paidMinutes = totalMinutes - breakMinutes;

        // walk through the shift one day at a time, like a taxi meter that switches rate at midnight. Adds up (rate x minutes) per day.
        BigDecimal rateMinutes = BigDecimal.ZERO;
        ZonedDateTime cursor = start.atZone(zone);
        ZonedDateTime finish = end.atZone(zone);

        while (cursor.isBefore(finish)) {
            ZonedDateTime nextMidnight = cursor.toLocalDate().plusDays(1).atStartOfDay(zone);
            ZonedDateTime pieceEnd = nextMidnight.isBefore(finish) ? nextMidnight : finish;

            long minutes = Duration.between(cursor, pieceEnd).toMinutes();
            BigDecimal rate = rateFor(job, cursor.toLocalDate(), holidays);
            rateMinutes = rateMinutes.add(rate.multiply(BigDecimal.valueOf(minutes)));

            cursor = pieceEnd;
        }

        // we don't know when the break was taken, so spread it evenly,
        // then turn minutes into hours. Divide once at the end so no cents are lost.
        return rateMinutes
                .multiply(BigDecimal.valueOf(paidMinutes))
                .divide(BigDecimal.valueOf(totalMinutes * 60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal rateFor(Job job, LocalDate date, Set<LocalDate> holidays) {
        //public holiday beats the weekend rate
        if (holidays.contains(date)) {
            return job.getPublicHolidayRate();
        }
        return switch (date.getDayOfWeek()) {
            case SATURDAY -> job.getSaturdayRate();
            case SUNDAY -> job.getSundayRate();
            default -> job.getWeekdayRate();
        };
    }
}
