package com.raichung.shiftledger.pay;

import com.raichung.shiftledger.job.Job;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PayCalculatorTest {

    private static final ZoneId ADELAIDE = ZoneId.of("Australia/Adelaide");

    private final PayCalculator calculator = new PayCalculator();

    // Weekday 30.50, Saturday 36.60, Sunday 42.70, public holiday 61.00
    private final Job job = new Job(1L, "Test Cafe",
            new BigDecimal("30.50"), new BigDecimal("36.60"),
            new BigDecimal("42.70"), new BigDecimal("61.00"));

    private Instant at(int year, int month, int day, int hour) {
        return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ADELAIDE).toInstant();
    }

    @Test
    void weekdayShiftWithBreak() {
        // Thursday 9am to 5pm, 30 min break: 7.5 h x 30.50
        BigDecimal pay = calculator.calculate(job, at(2026, 11, 5, 9), at(2026, 11, 5, 17), 30, ADELAIDE, Set.of());
        assertThat(pay).isEqualByComparingTo("228.75");
    }

    @Test
    void saturdayNightIntoSunday() {
        // Saturday 8pm to Sunday 2am: 4 h x 36.60 + 2 h x 42.70
        BigDecimal pay = calculator.calculate(job, at(2026, 11, 7, 20), at(2026, 11, 8, 2), 0, ADELAIDE, Set.of());
        assertThat(pay).isEqualByComparingTo("231.80");
    }

    @Test
    void daylightSavingEndsDuringShift() {
        // Clocks go back 1 hour early on Sunday 4 April 2027.
        // Saturday 10pm to Sunday 6am looks like 8 hours, but is really 9.
        // 2 h x 36.60 (Saturday) + 7 h x 42.70 (Sunday)
        BigDecimal pay = calculator.calculate(job, at(2027, 4, 3, 22), at(2027, 4, 4, 6), 0, ADELAIDE, Set.of());
        assertThat(pay).isEqualByComparingTo("372.10");
    }
    @Test
    void publicHolidayRate() {
        // Christmas Day 2026 (a Friday), 10am to 4pm: 6 h x 61.00
        Set<LocalDate> holidays = Set.of(LocalDate.of(2026, 12, 25));
        BigDecimal pay = calculator.calculate(job, at(2026, 12, 25, 10), at(2026, 12, 25, 16), 0, ADELAIDE, holidays);
        assertThat(pay).isEqualByComparingTo("366.00");
    }

    @Test
    void sundayNightIntoLabourDay() {
        // Sunday 4 Oct 2026 8pm to Labour Day Monday 2am: 4 h x 42.70 + 2 h x 61.00
        Set<LocalDate> holidays = Set.of(LocalDate.of(2026, 10, 5));
        BigDecimal pay = calculator.calculate(job, at(2026, 10, 4, 20), at(2026, 10, 5, 2), 0, ADELAIDE, holidays);
        assertThat(pay).isEqualByComparingTo("292.80");
    }
}
