package com.raichung.shiftledger.job;

import com.raichung.shiftledger.common.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobs;
    private final CurrentUser currentUser;

    public JobService(JobRepository jobs, CurrentUser currentUser) {
        this.jobs = jobs;
        this.currentUser = currentUser;
    }

    @Transactional
    public JobResponse create(CreateJobRequest req) {
        Job job = new Job(currentUser.id(), req.employerName(), req.weekdayRate(),
                req.saturdayRate(), req.sundayRate(), req.publicHolidayRate());
        return JobResponse.from(jobs.save(job));
    }

    @Transactional(readOnly = true)
    public List<JobResponse> list() {
        return jobs.findAllByUserIdOrderByEmployerNameAsc(currentUser.id())
                .stream().map(JobResponse::from).toList();
    }
}
