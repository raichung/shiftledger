package com.raichung.shiftledger.job;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String employerName;
    private BigDecimal weekdayRate;
    private BigDecimal saturdayRate;
    private BigDecimal sundayRate;
    private BigDecimal publicHolidayRate;

    protected Job() {} // JPA needs an empty constructor

    public Job(Long userId, String employerName, BigDecimal weekdayRate,
               BigDecimal saturdayRate, BigDecimal sundayRate, BigDecimal publicHolidayRate) {
        this.userId = userId;
        this.employerName = employerName;
        this.weekdayRate = weekdayRate;
        this.saturdayRate = saturdayRate;
        this.sundayRate = sundayRate;
        this.publicHolidayRate = publicHolidayRate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getEmployerName() { return employerName; }
    public BigDecimal getWeekdayRate() { return weekdayRate; }
    public BigDecimal getSaturdayRate() { return saturdayRate; }
    public BigDecimal getSundayRate() { return sundayRate; }
    public BigDecimal getPublicHolidayRate() { return publicHolidayRate; }
}
