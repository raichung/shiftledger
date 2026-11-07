package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.common.AppTime;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class FortnightHoursChecker {

    static final long LIMIT_MINUTES = 48 * 60;
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH);

    private final ShiftRepository shifts;

    public FortnightHoursChecker(ShiftRepository shifts) {
        this.shifts = shifts;
    }

    //visa fortnight is 14 days starting on a Monday. So every date sits inside two:
    //the one that started last week's Monday and the one starting this week's Monday.
    static List<LocalDate> fortnightStartsFor(LocalDate date) {
        LocalDate thisMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return List.of(thisMonday.minusWeeks(1), thisMonday);
    }

    public List<String> warningsFor(Long userId, Instant shiftStart) {
        LocalDate shiftDate = shiftStart.atZone(AppTime.ZONE).toLocalDate();
        List<String> warnings = new ArrayList<>();

        for (LocalDate fortnightStart : fortnightStartsFor(shiftDate)) {
            Instant from = fortnightStart.atStartOfDay(AppTime.ZONE).toInstant();
            Instant to = fortnightStart.plusWeeks(2).atStartOfDay(AppTime.ZONE).toInstant();

            long minutes = shifts.findAllStartingBetween(userId, from, to).stream()
                    .mapToLong(Shift::workedMinutes)
                    .sum();

            if (minutes > LIMIT_MINUTES) {
                warnings.add(String.format(Locale.ENGLISH,
                        "%.1f hours booked in the fortnight starting %s. The student visa limit is 48 hours during study periods.",
                        minutes / 60.0, DAY.format(fortnightStart)));
            }
        }
        return warnings;
    }
}