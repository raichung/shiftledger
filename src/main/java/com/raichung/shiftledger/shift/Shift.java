package com.raichung.shiftledger.shift;

import com.raichung.shiftledger.job.Job;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    private Instant startTime;
    private Instant endTime;
    private int breakMinutes;
    private String note;

    protected Shift() {}

    public Shift(Job job, Instant startTime, Instant endTime, int breakMinutes, String note) {
        this.job = job;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakMinutes = breakMinutes;
        this.note = note;
    }

    public Long getId() { return id; }
    public Job getJob() { return job; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public int getBreakMinutes() { return breakMinutes; }
    public String getNote() { return note; }
}
