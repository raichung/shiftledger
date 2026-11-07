package com.raichung.shiftledger.holiday;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, LocalDate> {

    // Only the dates, not whole rows. That's all the pay calculator needs.
    @Query("select h.holidayDate from PublicHoliday h")
    List<LocalDate> findAllDates();
}