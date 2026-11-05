package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.common.CurrentUser;
import com.raichung.shiftledger.job.Job;
import com.raichung.shiftledger.job.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shifts;
    private final JobRepository jobs;
    private final CurrentUser currentUser;

    public ShiftService(ShiftRepository shifts, JobRepository jobs, CurrentUser currentUser) {
        this.shifts = shifts;
        this.jobs = jobs;
        this.currentUser = currentUser;
    }

    @Transactional
    public ShiftResponse create(CreateShiftRequest req) {
        // Only find the job if it belongs to this user
        Job job = jobs.findByIdAndUserId(req.jobId(), currentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        if (!req.endTime().isAfter(req.startTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }

        int breakMinutes = req.breakMinutes() == null ? 0 : req.breakMinutes();
        Shift shift = new Shift(job, req.startTime().toInstant(), req.endTime().toInstant(),
                breakMinutes, req.note());
        return ShiftResponse.from(shifts.save(shift));
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> list() {
        return shifts.findAllByJob_UserIdOrderByStartTimeDesc(currentUser.id())
                .stream().map(ShiftResponse::from).toList();
    }
}
