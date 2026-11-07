package com.raichung.shiftledger.holiday;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "public_holidays")
public class PublicHoliday {

    @Id
    private LocalDate holidayDate;

    private String name;

    protected PublicHoliday() {}

    public LocalDate getHolidayDate() { return holidayDate; }
    public String getName() { return name; }
}