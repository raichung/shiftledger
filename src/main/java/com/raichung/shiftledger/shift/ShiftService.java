package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.common.AppTime;
import com.raichung.shiftledger.common.CurrentUser;
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
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shifts;
    private final JobRepository jobs;
    private final CurrentUser currentUser;
    private final PayCalculator payCalculator;

    public ShiftService(ShiftRepository shifts, JobRepository jobs,
                        CurrentUser currentUser, PayCalculator payCalculator) {
        this.shifts = shifts;
        this.jobs = jobs;
        this.currentUser = currentUser;
        this.payCalculator = payCalculator;
    }

    @Transactional
    public ShiftResponse create(CreateShiftRequest req) {
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
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> list() {
        return shifts.findAllByJob_UserIdOrderByStartTimeDesc(currentUser.id())
                .stream().map(this::toResponse).toList();
    }

    private ShiftResponse toResponse(Shift s) {
        BigDecimal pay = payCalculator.calculate(s.getJob(), s.getStartTime(), s.getEndTime(),
                s.getBreakMinutes(), AppTime.ZONE);
        return ShiftResponse.from(s, pay);
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
