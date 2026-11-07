package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.common.AppTime;
import com.raichung.shiftledger.common.CurrentUser;
import com.raichung.shiftledger.holiday.PublicHolidayRepository;
import com.raichung.shiftledger.job.Job;
import com.raichung.shiftledger.job.JobRepository;
import com.raichung.shiftledger.pay.PayCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class ShiftService {

    private final ShiftRepository shifts;
    private final JobRepository jobs;
    private final PublicHolidayRepository holidays;
    private final CurrentUser currentUser;
    private final PayCalculator payCalculator;
    private final FortnightHoursChecker fortnightChecker;

    public ShiftService(ShiftRepository shifts, JobRepository jobs, PublicHolidayRepository holidays,
                        CurrentUser currentUser, PayCalculator payCalculator,
                        FortnightHoursChecker fortnightChecker) {
        this.shifts = shifts;
        this.jobs = jobs;
        this.holidays = holidays;
        this.currentUser = currentUser;
        this.payCalculator = payCalculator;
        this.fortnightChecker = fortnightChecker;
    }

    @Transactional
    public CreateShiftResponse create(CreateShiftRequest req) {
        // Only find the job if it belongs to this user
        Job job = jobs.findByIdAndUserId(req.jobId(), currentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        Instant start = req.startTime().toInstant();
        Instant end = req.endTime().toInstant();
        int breakMinutes = req.breakMinutes() == null ? 0 : req.breakMinutes();

        // Rule 1: a shift must end after it starts
        if (!end.isAfter(start)) {
            throw badRequest("End time must be after start time");
        }

        // Rule 2: the break must be shorter than the shift
        if (breakMinutes >= Duration.between(start, end).toMinutes()) {
            throw badRequest("Break must be shorter than the shift");
        }

        // Rule 3: you can't be in two places at once (checked across all your jobs)
        if (shifts.countOverlapping(currentUser.id(), start, end) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This shift overlaps another shift");
        }

        Shift saved = shifts.save(new Shift(job, start, end, breakMinutes, req.note()));

        // Rule 4: warn, but don't block, if a visa fortnight goes over 48 hours.
        // Only a warning, because the limit doesn't apply during course breaks.
        List<String> warnings = fortnightChecker.warningsFor(currentUser.id(), start);

        return new CreateShiftResponse(toResponse(saved, holidayDates()), warnings);
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> list() {
        Set<LocalDate> holidayDates = holidayDates(); // load once, not once per shift
        return shifts.findAllByJob_UserIdOrderByStartTimeDesc(currentUser.id())
                .stream().map(s -> toResponse(s, holidayDates)).toList();
    }

    private Set<LocalDate> holidayDates() {
        return Set.copyOf(holidays.findAllDates());
    }

    private ShiftResponse toResponse(Shift s, Set<LocalDate> holidayDates) {
        BigDecimal pay = payCalculator.calculate(s.getJob(), s.getStartTime(), s.getEndTime(),
                s.getBreakMinutes(), AppTime.ZONE, holidayDates);
        return ShiftResponse.from(s, pay);
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}